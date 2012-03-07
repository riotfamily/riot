/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Riot.
 *
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.cachius;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.riotfamily.common.log.RiotLog;
import org.riotfamily.common.io.IOUtils;

/**
 * The Cachius cache.
 *
 * @author Felix Gnass
 */
public final class Cache implements Serializable {

    private RiotLog log = RiotLog.get(Cache.class);
    
    private static ItemUsageComparator itemUsageComparator = new ItemUsageComparator();
    
    private int size;

    private Map<String, CacheEntry> map;
    
    private Map<String, List<CacheItem>> taggedItems;
    
    private File cacheDir = null;

    private int currentDir = 0;
    
    private double evictionFactor = 0.2;
    
    private boolean enabled = true;

    private transient int capacity;
    
    private transient File[] dirs = null;

    private transient int numberOfDirs;
    
    private transient Object addItemlock;
    
    private transient CleanUpThread cleanUpThread;
        
    private transient volatile long lastOverflow = System.currentTimeMillis();
    
    private transient volatile long averageOverflowInterval;
    
    private transient volatile long maxInvalidationTime;
    
    /**
     * Create the cache.
     */
    public Cache(File cacheDir, int capacity, boolean enabled) {
        this.enabled = enabled;
        
        int mapCapacity = (int) (capacity / 0.75f) + 1;
        if (mapCapacity % 2 == 0) {
        	mapCapacity++;
        }
        
        this.map = new ConcurrentHashMap<String, CacheEntry>(mapCapacity); 
        this.taggedItems = new ConcurrentHashMap<String, List<CacheItem>>(mapCapacity);

        init();
        setCapacity(capacity);
        setCacheDir(cacheDir);
    }
    
    private void readObject(ObjectInputStream in)
    		throws ClassNotFoundException, IOException {
    	
  	     in.defaultReadObject();
  	     init();
    }
    
    /**
     * Initializes the instance upon creation or deserialization.
     */
    private void init() {
    	addItemlock = new Object();
    	cleanUpThread = new CleanUpThread();
    	cleanUpThread.start();
    }
    
    /**
     * Sets the directory where the cache items are stored. If a different
     * directory is already set, the old directory is emptied.
     * 
     * The method is not thread-safe and therefore protected.
     */
	protected final void setCacheDir(File cacheDir) {
		if (this.cacheDir != null) {
			clear();
		}
		this.cacheDir = cacheDir;
	}
	
    /**
     * Sets the cache capacity.
     * 
     * The method is not thread-safe and therefore protected.
     */
    protected final void setCapacity(int capacity) {
        this.capacity = capacity;
        numberOfDirs = capacity / 500;
        dirs = new File[numberOfDirs];
    }

    private void clear() {
        log.info("Removing all cache entries ...");
        map.clear();
        taggedItems.clear();
        size = 0;
        IOUtils.clearDirectory(cacheDir);
    }
    
    public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
    
    /**
     * Returns the CacheItem with the given key or creates a new one, if no
     * entry with that key exists.
     *
     * @param key The cache key
     * @return The CacheItem for the given key
     */
    public CacheEntry getItem(String key) {
    	if (!enabled) {
    		return null;
    	}
        CacheEntry entry = map.get(key);
        if (entry == null) {
        	synchronized (addItemlock) {
        		entry = map.get(key);
                if (entry == null) {
                	try {
	                	entry = new CacheEntry(key, getNextDir());
	                	map.put(key, entry);
	                	size++;
                	}
                	catch (Exception e) {
						log.error("Error creating CacheItem", e);
						return null;
					}
                }
        	}
        	checkCapacity();
        }
        entry.touch();
        return entry;
    }
    
    /**
     * Returns the directory where the next cache item should be created.
     * Note: Access to this method must be synchronized externally.
     */
    private File getNextDir() {
    	cacheDir.mkdirs();
        if (numberOfDirs <= 1) {
            return cacheDir;
        }
        if (currentDir >= numberOfDirs) {
            currentDir = 0;
        }
        File dir = dirs[currentDir];
        if (dir == null) {
            dir = new File(cacheDir, String.valueOf(currentDir));
            dir.mkdir();
            dirs[currentDir] = dir;
        }
        currentDir++;
        return dir;
    }
    
    /**
     * Notifies the clean-up thread when the capacity is exceeded.
     */
    private void checkCapacity() {
        if (size >= capacity) {
        	synchronized(cleanUpThread) {
        		cleanUpThread.notify();
        	}
        }
    }
    
    /**
     * Removes items from the cache that haven't been used for a long time.
     * The number of items removed is <code>capacity * evictionFactor</code>.
     */
    private void cleanup() {
    	log.info("Cache capacity exceeded. Performing cleanup ...");
    	long timeSinceLastOverflow = System.currentTimeMillis() - lastOverflow;
    	lastOverflow = System.currentTimeMillis();
    	if (averageOverflowInterval == 0) {
    		averageOverflowInterval = timeSinceLastOverflow;
    	}
    	else {
    		averageOverflowInterval = (averageOverflowInterval + timeSinceLastOverflow) / 2;
    	}
		ArrayList<CacheEntry> entries = new ArrayList<CacheEntry>(map.values());
		Collections.sort(entries, itemUsageComparator);
		int i = (int) Math.ceil(capacity * evictionFactor);
		Iterator<CacheEntry> it = entries.iterator();
		while (it.hasNext() && i > 0) {
			removeEntry(it.next());
			i--;
		}
    }
    
    /**
     * Removes the given item from the cache.
     */
    private void removeEntry(CacheEntry entry) {
    	synchronized (addItemlock) {
	    	map.remove(entry.getKey());
	    	size--;
	   		removeFromIndex(entry.getItem());
    	}
    	entry.delete();
    }

    /**
     * Returns a list of items tagged with the given String.
     */
    private List<CacheItem> getTaggedItems(String tag) {
    	return taggedItems.get(tag);
    }
    
    /**
     * Removes the item from the internal tag index.
     */
    protected void removeFromIndex(CacheItem item) {
    	Set<String> tags = item.getTags();
    	if (tags != null) {
    		for (String tag : tags) {
		    	List<CacheItem> items = getTaggedItems(tag);
		    	if (items != null) {
		    		synchronized (items) {
			    		items.remove(item);
			    		if (items.isEmpty()) {
			    			taggedItems.remove(tag);
			    		}
		    		}
		    	}
	    	}
    	}
    }
    
    /**
     * Adds the item to the internal tag index.  
     */
    protected void addToIndex(CacheItem item) {
    	Set<String> tags = item.getTags();
    	if (tags != null) {
    		for (String tag : tags) {
		    	List<CacheItem> items = getTaggedItems(tag);
		    	if (items == null) {
		    		items = new ArrayList<CacheItem>();
		    		taggedItems.put(tag, items);
		    	}
		    	synchronized (items) {
		    		items.add(item);
		    	}
    		}
    	}
    }


    /**
     * Invalidates all items tagged with the given String.
     */
    protected void invalidateTaggedItems(String tag) {
    	if (tag != null) {
	    	log.debug("Invalidating items tagged as " + tag);
	    	long t1 = System.currentTimeMillis();
	    	List<CacheItem> items = getTaggedItems(tag);
	    	if (items != null) {
	    		synchronized (items) {
	    			for (CacheItem item : items) {
						item.invalidate();
					}	
				}
	    	}
	    	long t2 = System.currentTimeMillis();
	    	long time = t2 - t1;
	    	if (time > maxInvalidationTime) {
	    		maxInvalidationTime = time;
	    	}
    	}
    }
    
    protected void invalidateAll() {
    	for (CacheEntry entry : map.values()) {
    		entry.getItem().invalidate();
    	}
    }
    
    public int getCapacity() {
		return capacity;
	}
    
    public int getSize() {
		return size;
	}
    
    public int getNumberOfTags() {
    	return taggedItems.size(); 
    }
    
    public long getAverageOverflowInterval() {
		return averageOverflowInterval;
	}
    
    public long getMaxInvalidationTime() {
		return maxInvalidationTime;
	}
    
    protected void resetOverflowStats() {
    	lastOverflow = 0;
    	averageOverflowInterval = 0;
    	maxInvalidationTime = 0;
    }

    /**
     * Comparator that compares items by their {@link CacheItem#getLastAccess() 
     * last-used} date.
     */
    private static class ItemUsageComparator implements Comparator<CacheEntry> {
    	public int compare(CacheEntry e1, CacheEntry e2) {
    		long l1 = e1.getLastAccess();
    		long l2 = e2.getLastAccess();
    		return (l1 < l2 ? -1 : (l1 == l2 ? 0 : 1));
    	}
    }
    
    public void shutdown() {
    	cleanUpThread.shutdown();
    }
    
    /**
     * Thread that performs a clean-up upon notification.
     */
    private class CleanUpThread extends Thread {
    	
    	private RiotLog log = RiotLog.get(CleanUpThread.class);
    	
    	private boolean running = true;
    	
    	public void run() {
    		while (running) {
	    		synchronized (this) {
					try {
						wait();
					}
					catch (InterruptedException e) {
						log.info("CleanUpThread interrupted.");
						break;
					}
				}
	    		if (running) {
	    			cleanup();
	    		}
    		}
    		log.info("CleanUpThread finished.");
    	}
    	
    	public synchronized void shutdown() {
    		log.info("Stopping CleanUpThread ...");
    		running = false;
    		notify();
    	}
    }

}

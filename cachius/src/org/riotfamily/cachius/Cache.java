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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.io.IOUtils;

/**
 * The Cachius cache.
 *
 * @author Felix Gnass
 */
public final class Cache implements Serializable {

    private Log log = LogFactory.getLog(Cache.class);
    
    private static ItemUsageComparator itemUsageComparator = new ItemUsageComparator();
    
    private int size;

    private Map<String, CacheItem> map;
    
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
    
    /**
     * Create the cache.
     */
    public Cache(File cacheDir, int capacity, boolean enabled) {
        this.enabled = enabled;
        
        int mapCapacity = (int) (capacity / 0.75f) + 1;
        if (mapCapacity % 2 == 0) {
        	mapCapacity++;
        }
        
        this.map = new ConcurrentHashMap<String, CacheItem>(mapCapacity); 
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
        numberOfDirs = capacity / 1000;
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
     * Returns whether an item with the given key exists and was not invalidated.
     */
    public boolean containsValidKey(String key) {
        if (map.containsKey(key)) {
            return !map.get(key).isNew();
        }
    	return false;
    }
    
    /**
     * Returns the CacheItem with the given key or creates a new one, if no
     * entry with that key exists.
     *
     * @param key The cache key
     * @return The CacheItem for the given key
     */
    public CacheItem getItem(String key) {
    	if (!enabled) {
    		return null;
    	}
        CacheItem item = map.get(key);
        if (item == null) {
        	synchronized (addItemlock) {
        		item = map.get(key);
                if (item == null) {
                	try {
	                	item = new CacheItem(key, getNextDir());
	                	map.put(key, item);
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
        item.touch();
        return item;
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
    	if (averageOverflowInterval == 0) {
    		averageOverflowInterval = timeSinceLastOverflow;
    	}
    	else {
    		averageOverflowInterval = (averageOverflowInterval + timeSinceLastOverflow) / 2;
    	}
		ArrayList<CacheItem> items = new ArrayList<CacheItem>(map.values());
		Collections.sort(items, itemUsageComparator);
		int i = (int) Math.ceil(capacity * evictionFactor);
		Iterator<CacheItem> it = items.iterator();
		while (it.hasNext() && i > 0) {
			removeItem(it.next());
			i--;
		}
    }
    
    /**
     * Removes the given item from the cache.
     */
    private void removeItem(CacheItem item) {
    	synchronized (addItemlock) {
	    	map.remove(item.getKey());
	    	size--;
	   		removeTags(item, item.getTags());			
	    	item.delete();
    	}
    }

    /**
     * Returns a list of items tagged with the given String.
     */
    private List<CacheItem> getTaggedItems(String tag) {
    	return taggedItems.get(tag);
    }
    
    /**
     * Removes the given item from all specified tag lists. If item is the last
     * entry in a tag-list, the whole list is removed from the taggedItems map.
     */
    protected void removeTags(CacheItem item, Set<String> tags) {
    	if (tags != null) {
    		for (String tag : tags) {
		    	List<CacheItem> items = getTaggedItems(tag);
		    	if (items != null) {
		    		items.remove(item);
		    		if (items.isEmpty()) {
		    			taggedItems.remove(tag);
		    		}
		    	}
	    	}
    	}
    }
    
    /**
     * Adds the given item to the specified tag lists.
     */
    protected void addTags(CacheItem item, Set<String> tags) {
    	if (tags != null) {
    		for (String tag : tags) {
		    	List<CacheItem> items = getTaggedItems(tag);
		    	if (items == null) {
		    		items = new ArrayList<CacheItem>();
		    		taggedItems.put(tag, items);
		    	}
		    	items.add(item);
	    	}
    	}
    }


    /**
     * Invalidates all items tagged with the given String.
     */
    protected void invalidateTaggedItems(String tag) {
    	if (tag != null) {
	    	log.debug("Invalidating items tagged as " + tag);
	    	List<CacheItem> items = getTaggedItems(tag);
	    	if (items != null) {
	    		for (CacheItem item : items) {
					item.invalidate();
				}
	    	}
    	}
    }
    
    protected void invalidateAll() {
    	for (CacheItem item : map.values()) {
    		item.invalidate();
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
    
    protected void resetOverflowStats() {
    	lastOverflow = 0;
    	averageOverflowInterval = 0;
    }

    /**
     * Comparator that compares items by their {@link CacheItem#getLastUsed() 
     * last-used} date.
     */
    private static class ItemUsageComparator implements Comparator<CacheItem> {
    	public int compare(CacheItem item1, CacheItem item2) {
    		long l1 = item1.getLastUsed();
    		long l2 = item2.getLastUsed();
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
    	
    	private Log log = LogFactory.getLog(CleanUpThread.class);
    	
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

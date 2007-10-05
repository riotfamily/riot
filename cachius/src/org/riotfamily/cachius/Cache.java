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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The cachius cache.
 *
 * @author Felix Gnass
 */
public final class Cache implements Serializable {

    private static final String CACHE_FILE = "cache-info";
    
    private static Log log = LogFactory.getLog(Cache.class);
    
    private int size;
    private int capacity;
    
    private transient CacheItem first;
    private transient CacheItem last;
    
    private transient HashMap map;
    private transient File cacheDir = null;
    private transient File[] dirs = null;
    
    private transient int numberOfDirs;
    private transient int currentDir = 0;
            
    /**
     * Create the cache.
     */
    private Cache(int capacity, File cacheDir) {
        this.size = 0;
        this.cacheDir = cacheDir;
        this.map = new HashMap(capacity);
        setCapacity(capacity);
        clear();
    }
    
    public File getCacheDir() {
        return cacheDir;
    }
    
    public void setCacheDir(File cacheDir) {
        if (this.cacheDir != null) {
            clear();
        }
        this.cacheDir = cacheDir;
    }
    
    /**
     * Returns the CacheItem with the given key or creates a new one, if no
     * entry with that key exists.
     * 
     * @param key The cache key
     * @return The CacheItem for the given key 
     */
    public synchronized CacheItem getItem(String key) {
        CacheItem item = (CacheItem) map.get(key);
        if (item == null) {
            item = newItem(key);
        }
        else {
            touch(item);
        }
        return item;
    }
    
    /**
     * Sets the cache capacity. If <code>capacity</code> is lower than the
     * current capacity, items are removed to fit the new size.
     */
    public synchronized void setCapacity(int capacity) {
        this.capacity = capacity;
        numberOfDirs = capacity / 1000;
        dirs = new File[numberOfDirs];
        while (size > capacity) {
            map.remove(last.getKey());
            unlink(last);
            size--;
        }
    }
    
    /**
     * Touches the given item, i.e. unlinks it and re-inserts it as the first
     * element of the list.
     */
    private void touch(CacheItem item) {
        unlink(item);
        link(item);
    }
    
    /**
     * Creates a new item for the given key and adds it to the cache. If due
     * to that operation the capacity is exeeded, the last item is removed. 
     */
    private CacheItem newItem(String key) {
        try {
        	if (capacity == 0) {
        		return null;
        	}
            log.debug("Creating new cache item for " + key);
            CacheItem item = createCacheItem(key);
            
            if (size >= capacity) {
                log.debug("Maximum size exceeded. Removing: " + last.getKey());
                map.remove(last.getKey());
                last.delete();
                unlink(last);
            }
            else {
                size++;
            }

            link(item);
            map.put(key, item);

            return item;
        }
        catch (IOException e) {
            log.error("Error creating item.", e);
            return null;
        }
    }

    /**
     * Inserts the given item at the begining of the item list.
     */
    private void link(CacheItem item) {
        item.setPrevious(null);
        item.setNext(first);
        if (first == null) {
            last = item;
        }
        else {
            first.setPrevious(item);
        }
        
        first = item;
    }
    
    /**
     * Removes the give item from the item list.
     */
    private void unlink(CacheItem item) {
        CacheItem previous = item.getPrevious();
        CacheItem next = item.getNext();
        
        if (previous != null) {
            previous.setNext(next);
        }
        else {
            first = next;
        }

        if (next != null) {
            next.setPrevious(previous);
        }
        else {
            last = previous;
        }
    }
    
    protected File getNextDir() {
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

    public CacheItem createCacheItem(String key) throws IOException {
        return new CacheItem(key, getNextDir());
    }
        
    private synchronized void clear() {
        log.info("Removing all cache entries ...");
        File[] entries = cacheDir.listFiles();
        for (int i = 0; i < entries.length; i++) {
            deleteFile(entries[i]);
        }
    } 
    
    private void deleteFile(File f) {
        if (f.isDirectory()) {
            File[] entries = f.listFiles();
            for (int i = 0; i < entries.length; i++) {
                deleteFile(entries[i]);
            }
        }
        f.delete();
    }
        
    public void invalidateTaggedItems(String tag) {
    	log.debug("Invalidating items taged as " + tag);
    	CacheItem item = first;
    	while (item != null) {
    		if (item.hasTag(tag)) {
				log.debug("Deleting CacheItem " + item.getKey());
				item.invalidate();
    		}
    		item = item.getNext();
    	}
    }
    
    /**
     * Factory method to create a new cache. If a cache file exists in the
     * given directory, the method will try to deserialize it. If no file
     * is found or the deserialization fails, a new cache is created. 
     */
    public static Cache newInstance(int capacity, File cacheDir, boolean restore) 
    		throws IOException {

		if (!cacheDir.exists() && !cacheDir.mkdirs()) {
		    throw new IOException("Can't create cache directory: " + cacheDir);
		}
		File f = new File(cacheDir, CACHE_FILE);
		if (restore && f.exists()) {
		    log.info("Trying to build cache from file: " + f);
		    try {
		        ObjectInputStream in = new ObjectInputStream(
		                 new FileInputStream(f));
		
		        Cache cache = (Cache) in.readObject();
		        in.close();
		        f.delete();
		        cache.setCacheDir(cacheDir);
		        cache.setCapacity(capacity);
		        log.info("Cache has been successfully deserialized. " +
		        		"Number of items: " + cache.size);
		        
		        return cache;
		    }
		    catch (InvalidClassException e) {
		    	log.info("Serialized cache has been discarded due to " +
		    			"version incompatibilies.");
		    }
		    catch (IOException e) {
		        log.warn("Deserialization failed.");
		    }
		    catch (ClassNotFoundException e) {
		        log.warn("Deserialization failed.", e);
		    }
		}
		log.info("Building new cache in: " + cacheDir);
		return new Cache(capacity, cacheDir);
	}
    
    /**
     * Serializes the cache state to disk.
     */
    public synchronized void persist() {
        File f = new File(cacheDir, CACHE_FILE);
        if (!f.exists()) {
            try {
                 log.info("Persisting the cache state ...");
                 ObjectOutputStream out = new ObjectOutputStream(
                         new FileOutputStream(f));
                 
                 out.writeObject(this);
                 out.close();
                 log.info("Cache state saved in " + f);
            }
            catch (IOException e) {
                log.error("Can't save cache state", e);
            }
        }
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException {
    	out.defaultWriteObject();
    	CacheItem item = first;
    	while (item != null) {
    		out.writeObject(item);
    		item = item.getNext();
    	}
    }
    
    private void readObject(ObjectInputStream in) throws IOException, 
            ClassNotFoundException {
         
        in.defaultReadObject();
        map = new HashMap(capacity);
        if (size > 0) {
	        CacheItem item = (CacheItem) in.readObject();
	        first = item;
	        CacheItem prev = item;
	        for (int i = 1; i < size; i++) {
	        	item = (CacheItem) in.readObject();
	        	item.setPrevious(prev);
	    		prev.setNext(item);
	        	map.put(item.getKey(), item);
	        	prev = item;
	        }
	        last = item;
        }
    }
        
}

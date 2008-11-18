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
 * Portions created by the Initial Developer are Copyright (C) 2007
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
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

/**
 * Class that holds a reference to the actual cached data and a lock that 
 * can be used to synchronize the access. It also keeps track of the last
 * access time so that least recently used items can be evicted if the 
 * cache capacity is exceeded.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class CacheEntry {

	/** The key used for lookups */
    private String key;
    
    private CacheItem item;
    
    /** Time of the last access */
    private long lastAccess;
    
    /** 
     * ReadWriteLock to prevent concurrent threads from updating
     * the cached content while others are reading.
     */
    private transient ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
    
    
    public CacheEntry(String key, File cacheDir) throws IOException {
    	this.key = key;
    	this.item = new CacheItem(cacheDir);
	}

	/**
     * Returns the key.
     */
    public String getKey() {
        return key;
    }
        
    public CacheItem getItem() {
		return item;
	}

	public void replaceItem(CacheItem newItem) {
		this.item.delete();
		this.item = newItem;
	}
	
	public CacheItem newItem() throws IOException {
		return new CacheItem(item.getDir());
	}
	
	/**
	 * Returns the lock. 
	 */
	protected ReentrantReadWriteLock getLock() {
		return this.lock;
	}
	
	/**
     * Sets the lastUsed timestamp to the current time.
     */
    protected void touch() {
    	this.lastAccess = System.currentTimeMillis();
    }
    
    /**
	 * Returns the last access time.
	 */
	public long getLastAccess() {
		return this.lastAccess;
	}
		
	
	protected void delete() {
    	WriteLock writeLock = lock.writeLock();
    	writeLock.lock();
    	try {
    		item.delete();
        }
        finally {
            writeLock.unlock();
        }
    }
	
	 /**
     * Calls <code>in.defaultReadObject()</code> and creates a new lock.
     */ 
    private void readObject(ObjectInputStream in) throws IOException, 
            ClassNotFoundException {
         
         in.defaultReadObject();
         lock = new ReentrantReadWriteLock(true);
    }
    
    public int hashCode() {
        return key.hashCode();
    }
    
    public boolean equals(Object obj) {
    	if (obj == this) {
    		return true;
    	}
    	if (obj instanceof CacheEntry) {
    		CacheEntry other = (CacheEntry) obj;
    		return key.equals(other.key);
    	}
    	return false;
    }
    
    public String toString() {
    	return key;
    }
}

package org.riotfamily.cachius;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;


/**
 * Class that is used as value object in the ConcurrentHashMap of a Cache.
 * Provides a ReadWriteLock to synchronize the access to the underlying 
 * CacheItem. Additionally it records when the item was accessed for the 
 * last time.
 */
public class CacheEntry implements Serializable, Comparable<CacheEntry> {
	
	/** The key used for lookups */
    private String key;
    
	/** The actual item */
    private CacheItem item;
    
    /** Time of the last access */
    private long lastAccess;
    
    /** 
     * ReadWriteLock to prevent concurrent threads from updating
     * the cached content while others are reading.
     */
    private transient ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
    
    CacheEntry(String key) {
    	this.key = key;
    	this.item = new CacheItem(key);
    }
    
    /**
     * Returns the key.
     */
    public String getKey() {
        return key;
    }
	
    public CacheItem getItem() {
    	touch();
		return item;
	}
    
    public void setItem(CacheItem item) {
    	touch();
		this.item = item;
	}
    
	/**
	 * Returns the lock. 
	 */
	protected ReentrantReadWriteLock getLock() {
		return this.lock;
	}
	
	/**
     * Sets the lastAccess timestamp to the current time.
     */
    private void touch() {
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
    
    @Override
	public int hashCode() {
        return key.hashCode();
    }
    
    @Override
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
    
    @Override
	public String toString() {
    	return key;
    }

	public int compareTo(CacheEntry that) {
		long l1 = this.lastAccess;
		long l2 = that.lastAccess;
		return (l1 < l2 ? -1 : (l1 == l2 ? 0 : 1));
	}
}

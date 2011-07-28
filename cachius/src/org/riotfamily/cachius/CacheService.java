package org.riotfamily.cachius;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.riotfamily.cachius.invalidation.DefaultItemInvalidator;
import org.riotfamily.cachius.invalidation.ItemIndex;
import org.riotfamily.cachius.invalidation.ItemInvalidator;
import org.riotfamily.cachius.persistence.DiskStore;
import org.riotfamily.cachius.persistence.SimpleDiskStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CacheService {

	private Logger log = LoggerFactory.getLogger(CacheService.class);

	private Map<String, Cache> caches = new HashMap<String, Cache>();
	
	private DiskStore diskStore;

	private ItemIndex index = new ItemIndex();
	
	private ItemInvalidator invalidator = new DefaultItemInvalidator();
	
	private CachiusStatistics stats;

	public CacheService() {
		this(new SimpleDiskStore());
	}
	
	public CacheService(DiskStore diskStore) {
		this(diskStore, Collections.singletonList(new Region("default")));
	}
	
	public CacheService(DiskStore diskStore, List<Region> regions) {
		this.diskStore = diskStore;
		this.stats = new CachiusStatistics(this);
		for (Region region : regions) {
			caches.put(region.getName(), new Cache(region, index));
		}
	}
	
	public CachiusStatistics getStatistics() {
		return stats;
	}	

	public Cache getCache(String region) {
		if (region == null) {
			region = "default";
		}
		Cache cache = caches.get(region);
		if (cache == null) {
			throw new IllegalArgumentException("No such cache region: " + region);
		}
		return cache;
	}

	private CacheEntry getCacheEntry(CacheHandler handler) {
		CacheEntry entry = null;
		String cacheKey = handler.getCacheKey();
		if (cacheKey != null) {
			Cache cache = getCache(handler.getCacheRegion());
			entry = cache.getEntry(cacheKey);
		}
		return entry;
	}
	
	public long getLastModified(CacheHandler handler) {
		CacheEntry entry = getCacheEntry(handler);
        if (entry != null) {
        	CacheItem item = entry.getItem();
        	if (item.isUpToDate(handler)) {
        		return item.getLastModified();
        	}
        }
        return handler.getLastModified();
	}
	
	public void handle(CacheHandler handler) throws Exception {
		CacheEntry entry = getCacheEntry(handler);
        if (entry == null) {
            handler.handleUncached();
        }
        else {
        	CacheItem item = entry.getItem();
        	if (item.isUpToDate(handler)) {
        		stats.addHit();
        		log.debug("Serving cached content: {}", entry.getKey());
        		serveData(handler, entry);
        	}
        	else {
        		stats.addMiss();
        		capture(entry, handler);        		        			
        	}
        }
	}
	
	private void capture(CacheEntry entry, CacheHandler handler) throws Exception {
    	CacheItem item = entry.getItem();
		long t1 = System.currentTimeMillis();
    	if (item != null && item.isServeStaleWhileRevalidate()) {
    		nonBlockingCapture(entry, handler);
    	}
    	else {
    		blockingCapture(entry, handler);
    	}
    	long t2 = System.currentTimeMillis();
    	stats.itemUpdated(item, t2 - t1);
    }
	
	 private void nonBlockingCapture(CacheEntry entry, CacheHandler handler)
	 		throws Exception {
	    	
    	CacheItem oldItem;
    	// Acquire a write-lock to replace the item by a temporary one
    	// that serves the old data.
    	WriteLock writeLock = entry.getLock().writeLock();
		writeLock.lock();
		try {
			oldItem = entry.getItem();
			if (oldItem.isUpToDate(handler)) {
				log.debug("Item has already been updated by another thread");
				serveData(handler, entry);
				return;
			}
			else {
				entry.setItem(new CacheItem(oldItem));					
			}
		}
		finally {
			// Release the write-lock unless it was already released by serveCacheEntry()
			if (entry.getLock().isWriteLockedByCurrentThread()) {
				writeLock.unlock();
			}
		}
		
		log.debug("Updating {} (non-blocking)", entry.getKey());
		
		// Create a new CacheItem and capture the content ...
		CacheItem newItem = new CacheItem(entry.getKey());

		updateInContext(handler, newItem);
		
		// Acquire a write-lock again to swap the CacheItems
		writeLock = entry.getLock().writeLock();
		writeLock.lock();
		try {
			replaceItemAndServeData(entry, handler, oldItem, newItem);
		}
		finally {
			// The lock should have already been released by serveCacheEntry(),
			// but in case an exception was thrown before, make sure the entry is unlocked.
			if (entry.getLock().isWriteLockedByCurrentThread()) {
				writeLock.unlock();
			}
		}	
	}
	 
    private void blockingCapture(CacheEntry entry, CacheHandler handler)
    		throws Exception {
    	
    	WriteLock writeLock = entry.getLock().writeLock();
		writeLock.lock();
		try {
			CacheItem oldItem = entry.getItem();
			if (oldItem.isUpToDate(handler)) {
				log.debug("Item has already been updated by another thread");
				serveData(handler, entry);
			}
			else {
				// Item is stale and must be revalidated
				log.debug("Updating {} (blocking)", entry.getKey());
				CacheItem newItem = new CacheItem(entry.getKey());
				updateInContext(handler, newItem);
				replaceItemAndServeData(entry, handler, oldItem, newItem);
			}
		}
		finally {
			// The lock should have already been released by serveData(),
			// but in case an exception was thrown before, make sure the entry is unlocked.
			if (entry.getLock().isWriteLockedByCurrentThread()) {
				writeLock.unlock();
			}
		}
    }
    
    private void updateInContext(CacheHandler handler, CacheItem newItem)
    		throws Exception {
    	
    	CacheItem parent = CacheContext.getItem();
    	try { 
	    	CacheContext.setItem(newItem);
			newItem.setData(handler.capture(diskStore));
    	}
    	finally {
    		CacheContext.setItem(parent);
    	}
    }
    
    private void replaceItemAndServeData(CacheEntry entry, CacheHandler handler,
			CacheItem oldItem, CacheItem newItem) throws Exception {
		
		if (!newItem.isError() || !oldItem.isServeStaleOnError()) {
			entry.setItem(newItem);
		}
		serveData(handler, entry);
		
		if (newItem.isError()) {
			entry.setItem(oldItem);
			newItem.delete();
		}
		else {
			index.remove(oldItem);
			index.add(newItem);
			oldItem.delete();
		}
	}
    
    /**
     * Serves the cached content. Acquires a read-lock for the given entry.
     * if the current thread already has a write-lock, the lock is down-graded.
     * When the method returns, all locks will be released.
     */
    private void serveData(CacheHandler handler, CacheEntry entry) 
    		throws Exception {
    	
		ReadLock readLock = entry.getLock().readLock();
        readLock.lock();
        try {
        	if (entry.getLock().isWriteLockedByCurrentThread()) {
        		entry.getLock().writeLock().unlock();
        	}
        	CacheItem item = entry.getItem();
        	handler.serve(item.getData());
        	CacheItem parentItem = CacheContext.getItem();
        	if (parentItem != null) {
        		parentItem.addAll(item);
        	}
        }
        finally {
        	readLock.unlock();	
        }
    }

	public void invalidateTaggedItems(String tag) {
		invalidator.invalidate(index, tag);
	}

	public void destroy() throws Exception {
		for (Cache cache : caches.values()) {
			cache.destroy();
		}
	}
    
}

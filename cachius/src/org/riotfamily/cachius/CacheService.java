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

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.riotfamily.common.log.RiotLog;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @author Alf Werder [alf dot werder at artundweise dot de]
 * @since 6.5
 */
public class CacheService {

	private RiotLog log = RiotLog.get(CacheService.class);
	
	private Cache cache;
	
	private boolean checkInvolvedFiles;
	
	private boolean staleUnlessExpired;
	
	private boolean staleWhileRevalidate;
	
	private CachiusStatistics stats;

	private ThreadLocal<DeferredCacheInvalidator> deferredCacheInvalidators;
	
	private CacheInvalidator defaultCacheInvalidator; 
	
	public CacheService(Cache cache) {
		this.cache = cache;
		this.stats = new CachiusStatistics(this);

		this.deferredCacheInvalidators =
			new ThreadLocal<DeferredCacheInvalidator>();
		this.defaultCacheInvalidator = new ImmediateCacheInvalidator();
	}
	
	public CachiusStatistics getStatistics() {
		return stats;
	}
	
	protected Cache getCache() {
		return cache;
	}
		
	public void setCheckInvolvedFiles(boolean checkInvolvedFiles) {
		this.checkInvolvedFiles = checkInvolvedFiles;
	}
	
	/**
	 * Sets whether cached data with a set expiration date should be served
	 * until it expires even if it has been invalidated.  
	 */
    public void setStaleUnlessExpired(boolean staleUnlessExpired) {
		this.staleUnlessExpired = staleUnlessExpired;
	}

    /**
     * Sets whether stale data should be served during revalidation. If set to 
     * <code>false</code> concurrent threads will wait until the content has
     * been updated. If set to <code>true</code> only the first thread has to
     * wait while others will still see the old version.
     */
	public void setStaleWhileRevalidate(boolean staleWhileRevalidate) {
		this.staleWhileRevalidate = staleWhileRevalidate;
	}

	/**
     * Immediately invalidates all items tagged with the given String.
     */
	protected void immediatelyInvalidateTaggedItems(String tag) {
        cache.invalidateTaggedItems(tag);
    }

	public long getLastModified(CacheHandler handler) {
		CacheEntry entry = null;
		String cacheKey = handler.getCacheKey();
		if (cacheKey != null) {
			entry = cache.getItem(cacheKey);
		}
		if (entry != null) {
			CacheItem item = entry.getItem();
			if (!item.isExpired()) {
	        	return item.getLastModified();
			}
		}
		try {
			return handler.getLastModified();
		}
		catch (Exception e) {
			log.error("Error invoking the last-modified method", e);
			return -1L;
		}
	}
	
	public void handle(CacheHandler handler) throws Exception {
		CacheEntry entry = null;
		String cacheKey = handler.getCacheKey();
		if (cacheKey != null) {
			entry = cache.getItem(cacheKey);
		}
        if (entry == null) {
            handler.handleUncached();
        }
        else {
        	CacheItem item = entry.getItem();
        	long mtime = getModificationTime(item, handler);
        	if (mtime > item.getLastModified()|| (item.isInvalidated() 
        			&& mtime == item.getLastModified())) {
        		
        		stats.addMiss();
        		capture(entry, mtime, handler);
        	}
        	else {
        		stats.addHit();
        		if (!serveCacheEntry(handler, entry)) {
        			// The rare case, that the item was deleted due to a cleanup
       				capture(entry, mtime, handler);        			
        		}
        		else {
        			TaggingContext.inheritFrom(item);
        		}
        	}
        }
	}
	
	private long getModificationTime(CacheItem item,
    		CacheHandler handler) throws Exception {
		
		long mtime = getHandlerModificationTime(item, handler);
		if (checkInvolvedFiles) {
			mtime = Math.max(mtime, item.getLastFileModification());
		}
		return mtime;
	}
	
	private boolean mustRevalidate(CacheItem cacheItem) {
		return cacheItem.isExpired() || (cacheItem.isInvalidated() 
				&& !staleUnlessExpired);
	}
	
	private long getHandlerModificationTime(CacheItem cacheItem,
    		CacheHandler handler) throws Exception {

		if (mustRevalidate(cacheItem)) {
			long mtime = handler.getLastModified();
			if (mtime < 0) {
				log.warn("Handler [%s] returned a negative lastModified value: %s", handler, mtime);
				mtime = System.currentTimeMillis();
			}
			if (mtime == cacheItem.getLastModified()) {
				cacheItem.setTimeToLive(handler.getTimeToLive());
			}
			return mtime;
		}
		return cacheItem.getLastModified();
    }
	
    
    private void capture(CacheEntry entry, long mtime, 
    		CacheHandler handler) throws Exception {
    	
    	CacheItem item = entry.getItem();
    	if (log.isDebugEnabled()) {
    		log.debug("Updating cache item %s", item);
    	}
		long t1 = System.currentTimeMillis();
    	if (staleWhileRevalidate && item.exists()) {
    		nonBlockingCapture(entry, mtime, handler);
    	}
    	else {
    		blockingCapture(entry, mtime, handler);
    	}
    	long t2 = System.currentTimeMillis();
    	stats.itemUpdated(item, t2 - t1);
    }
    
    private void nonBlockingCapture(CacheEntry entry, long mtime, 
    		CacheHandler handler) throws Exception {
    	
    	CacheItem oldItem;
    	WriteLock writeLock = entry.getLock().writeLock();
		writeLock.lock();
		try {
			oldItem = entry.getItem();
			if (oldItem.getLastModified() > mtime) {
				log.debug("Item has already been updated by another thread");
				TaggingContext.inheritFrom(oldItem);
				serveCacheEntry(handler, entry);
				return;
			}
			else {
				oldItem.setLastModified(mtime);
				oldItem.setTimeToLive(handler.getTimeToLive());
			}
		}
		finally {
			if (entry.getLock().isWriteLockedByCurrentThread()) {
				writeLock.unlock();
			}
		}
		
		log.debug("Performing non-blocking update ...");
		CacheItem newItem = entry.newItem();
		boolean update = updateCacheItem(handler, oldItem, newItem);
		
		if (update) {
			writeLock = entry.getLock().writeLock();
			writeLock.lock();
			try {
				newItem.setLastModified(mtime);
				newItem.setTimeToLive(handler.getTimeToLive());
				entry.replaceItem(newItem);
				serveCacheEntry(handler, entry);
			}
			finally {
				if (entry.getLock().isWriteLockedByCurrentThread()) {
					writeLock.unlock();
				}
			}	
		}
		else {
			TaggingContext.inheritFrom(newItem);
			handler.writeCacheItem(newItem);
			newItem.delete();
		}
    }
    
    private void blockingCapture(CacheEntry entry, long mtime, 
    		CacheHandler handler) throws Exception {
    	
    	WriteLock writeLock = entry.getLock().writeLock();
		writeLock.lock();
		try {
			CacheItem item = entry.getItem();
			if (item.getLastModified() >= mtime) {
				TaggingContext.inheritFrom(item);
			}
			else {
				if (updateCacheItem(handler, item, item)) {
					item.setLastModified(mtime);
					item.setTimeToLive(handler.getTimeToLive());
				}
			}
			serveCacheEntry(handler, entry);
		}
		finally {
			if (entry.getLock().isWriteLockedByCurrentThread()) {
				writeLock.unlock();
			}
		}
    }
    
    private boolean updateCacheItem(CacheHandler handler,
    		CacheItem oldItem, CacheItem newItem) throws Exception {
    	
    	TaggingContext ctx = TaggingContext.openNestedContext();
		boolean update = handler.updateCacheItem(newItem);
		ctx.close();
		if (update && !ctx.isPreventCaching()) {
			cache.removeFromIndex(oldItem);
			
			newItem.setTags(ctx.getTags());
			cache.addToIndex(newItem);
			
			if (checkInvolvedFiles) {
				newItem.setInvolvedFiles(ctx.getInvolvedFiles());
			}
			else {
				newItem.setInvolvedFiles(null);
			}
			return true;
		}
		else {
			newItem.invalidate();
			return false;
		}
    }
    
    private boolean serveCacheEntry(CacheHandler handler, CacheEntry entry) 
    		throws IOException {
    	
    	if (log.isDebugEnabled()) {
    		log.debug("Serving cached content: " + entry.getKey());
    	}
		ReadLock readLock = entry.getLock().readLock();
        readLock.lock();
        try {
        	if (entry.getLock().isWriteLockedByCurrentThread()) {
        		entry.getLock().writeLock().unlock();
        	}
        	CacheItem item = entry.getItem();
			handler.writeCacheItem(item);
        }
        finally {
        	readLock.unlock();	
        }
        return true;
    }
        
	protected CacheInvalidator getCacheInvalidator() {
		CacheInvalidator cacheInvalidator = deferredCacheInvalidators.get();
		if (cacheInvalidator == null) {
			cacheInvalidator = defaultCacheInvalidator;
		}
		return cacheInvalidator;
	}

	/**
	 * Enables locally deferred cache invalidation for the current thread.
	 * After a call to this method, all calls to
	 * {@see #invalidateTaggedItems(String)} will no longer
	 * immediately invalidate any cache item. Instead the tag will get stored
	 * until {@see #commitLocallyDeferredInvalidation()} is called.
	 * 
	 *  It's save to call this method more then once in a single thread, as long
	 *  as you have a balanced number of begin and commit calls. In other words:
	 *  Nested bocks auf deferred cache invalidation are supported.
	 *  
	 *  @see #invalidateTaggedItems(String)
	 *  @see #commitLocallyDeferredInvalidation()
	 *  @since 8.0.1  
	 */
	public void beginLocallyDeferredInvalidation() {
		if (log.isDebugEnabled()) {
			log.info("Begin locally deferred cache invalidation.");
		}
		deferredCacheInvalidators.set(
			new DeferredCacheInvalidator(deferredCacheInvalidators.get()));
	}
	
	/**
	 * Ends locally deferred cache invalidation for the current thread. All tags
	 * stores since the last call to {@see #invalidateTaggedItems(String)} get
	 * invalidated in a bulk operation and this {@CacheService} is put to
	 * immediate cache invalidation mode again.
	 * 
	 * If you called {@see #beginLocallyDeferredInvalidation()} more than once
	 * for the current thread, you have to make sure that a balanced number of
	 * begins and commits will occur. Only the last commit will invalidate any
	 * cache item.  
	 * 
	 *  @see #invalidateTaggedItems(String)
	 *  @see #beginLocallyDeferredInvalidation()
	 *  @since 8.0.1  
	 */
	public void commitLocallyDeferredInvalidation() {
		DeferredCacheInvalidator deferredCacheInvalidator =
			deferredCacheInvalidators.get();
		
		if (deferredCacheInvalidator != null) {
			log.info("Commit locally deferred cache invalidation.");

			deferredCacheInvalidator.commit();
			deferredCacheInvalidators.set(deferredCacheInvalidator.getParent());
		} else {
			log.warn("Unbalanced commit of locally deferred cache " +
				"invalidation is ignored.");
		}
	}

	public void invalidateTaggedItems(String tag) {
		getCacheInvalidator().invalidateTaggedItems(tag);
	}
	
	private interface CacheInvalidator {
		public void invalidateTaggedItems(String tag);
	}
	
	private class ImmediateCacheInvalidator implements CacheInvalidator {
		public void invalidateTaggedItems(String tag) {
			if (log.isDebugEnabled()) {
				log.debug(MessageFormat.format(
					"Immediately invalidating items tagged as {0}.", tag));
			}
			
			CacheService.this.immediatelyInvalidateTaggedItems(tag);
		}
	}
	
	private class DeferredCacheInvalidator implements CacheInvalidator {
		private Set<String> tags;
		private DeferredCacheInvalidator parent;
		
		protected CacheInvalidator getDownstreamInvalidator() {
			if (parent != null) {
				return parent;
			}
			
			return CacheService.this.defaultCacheInvalidator;
		}
		
		public DeferredCacheInvalidator(DeferredCacheInvalidator parent) {
			tags = new HashSet<String>();
			this.parent = parent;
		}
		
		public DeferredCacheInvalidator getParent() {
			return parent;
		}

		public void invalidateTaggedItems(String tag) {
			if (log.isDebugEnabled()) {
				log.debug(MessageFormat.format(
					"Storing tag {0} for later invalidation of items tagged " +
					"with it.", tag));
			}
			tags.add(tag);
		}
		
		public void commit() {
			if (log.isDebugEnabled()) {
				log.info(MessageFormat.format(
					"Performing invalidation of items tagged with one out of " +
					"{0} stored tags.", tags.size()));
			}

			CacheInvalidator invalidator = getDownstreamInvalidator();
			for (String tag : tags) {
				invalidator.invalidateTaggedItems(tag);
			}
		}
	}
}

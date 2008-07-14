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
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.cachius.spring.CacheableController;
import org.riotfamily.cachius.support.ReaderWriterLock;
import org.riotfamily.common.util.Generics;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class CacheService {

	private static Log log = LogFactory.getLog(CacheService.class);
	
	private Cache cache;
	
	private boolean checkInvolvedFiles = true;
	
	public CacheService(Cache cache) {
		this.cache = cache;
	}
		
    /**
     * Invalidates all items tagged with the given String.
     */
    public void invalidateTaggedItems(String tag) {
        cache.invalidateTaggedItems(tag);
    }

	public long getLastModified(CacheHandler callback) {
		CacheItem cacheItem = null;
		String cacheKey = callback.getCacheKey();
		if (cacheKey != null) {
			cacheItem = cache.getItem(cacheKey);
		}
		if (cacheItem != null) {
			if (!cacheItem.isNew()) {
				long now = System.currentTimeMillis();
		        long ttl = callback.getTimeToLive();
		        if (ttl == CacheableController.CACHE_ETERNALLY
		        		|| cacheItem.getLastCheck() + ttl <= now) {

		        	return cacheItem.getLastModified();
		        }
			}
		}
		try {
			return callback.getLastModified();
		}
		catch (Exception e) {
			log.error("Error invoking the last-modified method", e);
			return -1L;
		}
	}
	
	public void handle(CacheHandler handler) throws Exception {
		CacheItem cacheItem = null;
		String cacheKey = handler.getCacheKey();
		if (cacheKey != null) {
			cacheItem = cache.getItem(cacheKey);
		}
        if (cacheItem == null) {
            handler.handleUncached();
        }
        else {
        	long mtime = getModificationTime(cacheItem, handler);
        	if (mtime > cacheItem.getLastModified()) {
        		capture(cacheItem, mtime, handler);
        	}
        	else {
        		if (!writeCacheItem(handler, cacheItem)) {
        			// The rare case, that the item was deleted due to a cleanup
       				capture(cacheItem, mtime, handler);        			
        		}
        	}
        }
	}
	
	private long getModificationTime(CacheItem cacheItem,
    		CacheHandler handler) throws Exception {
		
		long mtime = getHandlerModificationTime(cacheItem, handler);
		if (checkInvolvedFiles) {
			mtime = Math.max(mtime, cacheItem.getLastFileModified());
		}
		return mtime;
	}
	
	private long getHandlerModificationTime(CacheItem cacheItem,
    		CacheHandler handler) throws Exception {

    	long now = System.currentTimeMillis();
    	
        // No need to check if the item has just been constructed or
        // the cache file has been deleted
        if (cacheItem.isNew() || !cacheItem.exists()) {
            return now;
        }

        long ttl = handler.getTimeToLive();
        if (ttl == CacheableController.CACHE_ETERNALLY) {
        	return 0;
        }
        if (cacheItem.getLastCheck() + ttl < now) {
	        long mtime = handler.getLastModified();
	        cacheItem.setLastCheck(now);
	        if (mtime > cacheItem.getLastModified()) {
	            return mtime;
	        }
        }
       	return cacheItem.getLastModified();
    }
	
    
    private void capture(CacheItem cacheItem, long mtime, 
    		CacheHandler callback) throws Exception {
    	
    	if (log.isDebugEnabled()) {
    		log.debug("Updating cache item " + cacheItem.getKey());
    	}
		
		ReaderWriterLock lock = cacheItem.getLock();
		try {
			// Acquire a writer lock ...
			lock.lockForWriting();
			// Check if another writer has already updated the item
			if (mtime > cacheItem.getLastModified()) {
				TaggingContext ctx = TaggingContext.openNestedContext();
				boolean update = callback.updateCacheItem(cacheItem);
				ctx.close();
				if (update && !ctx.isPreventCaching()) {
					Set<String> oldTags = cacheItem.getTags();
					Set<String> tags = ctx.getTags();
					cacheItem.setTags(tags);
					Set<String> newTags = Generics.newHashSet();
					if (tags != null) {
						newTags.addAll(tags);
					}
					Iterator<String> it = newTags.iterator();
					while (it.hasNext()) {
						String tag = it.next();
						boolean existingTag = oldTags != null && oldTags.remove(tag);
						if (existingTag) {
							it.remove();
						}
					}
					cache.removeTags(cacheItem, oldTags);
					cache.addTags(cacheItem, newTags);
					if (checkInvolvedFiles) {
						cacheItem.setInvolvedFiles(ctx.getInvolvedFiles());
					}
					else {
						cacheItem.setInvolvedFiles(null);
					}
					cacheItem.setLastModified(mtime);
				}
				else {
					cacheItem.invalidate();
				}
			}
			else {
				log.debug("Item has already been updated by another thread");
			}
			callback.writeCacheItem(cacheItem);
		}
		finally {
			lock.releaseWriterLock();
		}
    }
   
    private boolean writeCacheItem(CacheHandler callback,
    		CacheItem cacheItem) throws IOException {
    	
    	if (log.isDebugEnabled()) {
    		log.debug("Serving cached version of " + cacheItem.getKey());
    	}
    	
    	ReaderWriterLock lock = cacheItem.getLock();
    	try {
    		// Acquire a reader lock and serve the cached version
    		lock.lockForReading();
    		if (!cacheItem.exists()) {
        		return false;
        	}
        	callback.writeCacheItem(cacheItem);
    	}
    	finally {
    		lock.releaseReaderLock();
    	}
    	return true;
    }
    
}

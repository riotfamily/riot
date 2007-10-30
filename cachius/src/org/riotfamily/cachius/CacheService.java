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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.cachius.spring.CacheableController;
import org.riotfamily.cachius.support.ReaderWriterLock;
import org.riotfamily.cachius.support.SessionIdEncoder;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class CacheService {

	private static Log log = LogFactory.getLog(CacheService.class);
	
	private Cache cache;
	
	public CacheService(Cache cache) {
		this.cache = cache;
	}

	public long getLastModified(HttpServletRequest request, 
			CacheableRequestProcessor processor) {
		
		String cacheKey = processor.getCacheKey(request);
		SessionIdEncoder sessionIdEncoder = new SessionIdEncoder(request);
		CacheItem cacheItem = getCacheItem(cacheKey, sessionIdEncoder);
		if (cacheItem != null) {
			if (!cacheItem.isNew()) {
				long now = System.currentTimeMillis();
		        long ttl = processor.getTimeToLive();
		        if (ttl == CacheableController.CACHE_ETERNALLY
		        		|| cacheItem.getLastCheck() + ttl <= now) {

		        	return cacheItem.getLastModified();
		        }
			}
    		try {
    			return processor.getLastModified(request);
    		}
    		catch (Exception e) {
    			log.error("Error invoking the last-modified method", e);
    		}
		}
		return -1L;
	}
	
	public void serve(HttpServletRequest request, HttpServletResponse response, 
			CacheableRequestProcessor processor) throws Exception {
		
		String cacheKey = processor.getCacheKey(request);
		SessionIdEncoder sessionIdEncoder = new SessionIdEncoder(request);
		CacheItem cacheItem = getCacheItem(cacheKey, sessionIdEncoder);
		
        if (cacheItem == null) {
            processor.processRequest(request, response);
        }
        else {
        	long mtime = getLastModified(cacheItem, processor, request);
        	if (mtime > cacheItem.getLastModified()) {
        		capture(cacheItem, request, response, sessionIdEncoder, mtime, processor);
        	}
        	else {
        		serve(cacheItem, request, response, sessionIdEncoder);
        	}
        }
	}
	
	private CacheItem getCacheItem(String cacheKey, 
			SessionIdEncoder sessionIdEncoder) {
		
        if (cacheKey == null) {
            log.debug("Cache key is null - Response won't be cached");
            return null;
        }

        if (sessionIdEncoder.urlsNeedEncoding()) {
            cacheKey += ";jsessionid";
        }
        
        CacheItem cacheItem = cache.getItem(cacheKey);
        if (cacheItem != null && (cacheItem.isNew() || !cacheItem.exists())) {
        	cacheItem.setFilterSessionId(sessionIdEncoder.urlsNeedEncoding());
        }
        return cacheItem;
    }
	
	/**
     * 
     */
    private long getLastModified(CacheItem cacheItem,
    		CacheableRequestProcessor processor, HttpServletRequest request)
    		throws Exception {

    	long now = System.currentTimeMillis();
    	
        // No need to check if the item has just been constructed or
        // the cache file has been deleted
        if (cacheItem.isNew() || !cacheItem.exists()) {
            return now;
        }

        long ttl = processor.getTimeToLive();
        if (ttl == CacheableController.CACHE_ETERNALLY) {
        	return 0;
        }
        if (cacheItem.getLastCheck() + ttl < now) {
	        long mtime = processor.getLastModified(request);
	        cacheItem.setLastCheck(now);
	        if (mtime > cacheItem.getLastModified()) {
	            return mtime;
	        }
        }
       	return cacheItem.getLastModified();
    }
    
    
    private void capture(CacheItem cacheItem, 
    		HttpServletRequest request, HttpServletResponse response,
    		SessionIdEncoder sessionIdEncoder, long mtime, 
    		CacheableRequestProcessor processor) throws Exception {
    	
    	if (log.isDebugEnabled()) {
    		log.debug("Updating cache item " + cacheItem.getKey());
    	}
		CachiusResponseWrapper wrapper = new CachiusResponseWrapper(
				response, cacheItem, sessionIdEncoder);
		
		ReaderWriterLock lock = cacheItem.getLock();
		try {
			// Acquire a writer lock ...
			lock.lockForWriting();
			// Check if another writer has already updated the item
			if (mtime > cacheItem.getLastModified()) {
				TaggingContext.openNestedContext(request);
				processor.processRequest(request, wrapper);
				cacheItem.setTags(TaggingContext.popTags(request));
				wrapper.stopCapturing();
				cacheItem.setLastModified(mtime);
			}
			else {
				log.debug("Item has already been updated by another thread");
			}
			cacheItem.writeTo(response, sessionIdEncoder.getSessionId());
		}
		finally {
			lock.releaseWriterLock();
		}
    }
   
    private void serve(CacheItem cacheItem, HttpServletRequest request, 
            HttpServletResponse response, SessionIdEncoder sessionIdEncoder) 
    		throws IOException {
    	
    	if (log.isDebugEnabled()) {
    		log.debug("Serving cached version of " + cacheItem.getKey());
    	}
    	
    	ReaderWriterLock lock = cacheItem.getLock();
    	try {
    		// Acquire a reader lock and serve the cached version
    		lock.lockForReading();
        	cacheItem.writeTo(response, sessionIdEncoder.getSessionId());
    	}
    	finally {
    		lock.releaseReaderLock();
    	}
    }
    
    public boolean isCached(String key) {
    	return cache.containsKey(key);
    }
    
}

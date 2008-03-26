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
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.cachius.spring.CacheableController;
import org.riotfamily.cachius.support.ReaderWriterLock;
import org.riotfamily.cachius.support.ServletUtilsExt;
import org.riotfamily.cachius.support.SessionCreationPreventingRequestWrapper;
import org.riotfamily.cachius.support.SessionIdEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

/**
 * Backport of the new (6.5+) cache to the 6.4 branch.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @author Alf Werder [alf dot werder at artundweise dot de]
 * @since 6.4.4
 */
public class CacheService {

	private static Pattern IE_MAJOR_VERSION_PATTERN = 
		Pattern.compile("^Mozilla/\\d\\.\\d+ \\(compatible[-;] MSIE (\\d)");

	private static Pattern BUGGY_NETSCAPE_PATTERN = 
		Pattern.compile("^Mozilla/4\\.0[678]");

	private static Log log = LogFactory.getLog(CacheService.class);
	
	private Cache cache;
	
	public CacheService(Cache cache) {
		this.cache = cache;
	}

	public long getLastModified(HttpServletRequest request, 
			CacheableRequestProcessor processor) {
		
		String cacheKey = processor.getCacheKey(request);
		boolean zip = processor.responseShouldBeZipped(request) 
				&& responseCanBeZipped(request);
		
		SessionIdEncoder sessionIdEncoder = new SessionIdEncoder(request);
		CacheItem cacheItem = getCacheItem(cacheKey, sessionIdEncoder, zip);
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
		
		boolean shouldZip = processor.responseShouldBeZipped(request); 
		boolean zip = shouldZip && responseCanBeZipped(request);
		
		String cacheKey = processor.getCacheKey(request);
		SessionIdEncoder sessionIdEncoder = new SessionIdEncoder(request);
		CacheItem cacheItem = getCacheItem(cacheKey, sessionIdEncoder, zip);
		
        if (cacheItem == null) {
        	log.debug("No CacheItem for " 
        			+ ServletUtilsExt.getRequestUri(request) 
        			+ " - Response won't be cached.");
        	
            processor.processRequest(request, response);
        }
        else {
        	long mtime = getLastModified(cacheItem, processor, request);
        	if (mtime > cacheItem.getLastModified()) {
        		capture(cacheItem, request, response, sessionIdEncoder, mtime, processor, shouldZip, zip);
        	}
        	else {
        		if (!serve(cacheItem, request, response, sessionIdEncoder)) {
        			// The rare case, that the item was deleted due to a cleanup
       				capture(cacheItem, request, response, sessionIdEncoder, mtime, processor, shouldZip, zip);        			
        		}
        	}
        }
	}
	
	private CacheItem getCacheItem(String cacheKey, 
			SessionIdEncoder sessionIdEncoder, boolean zip) {
		
        if (cacheKey == null) {
            return null;
        }

        if (sessionIdEncoder.urlsNeedEncoding()) {
            cacheKey += ";jsessionid";
        }
        
        if (zip) {
        	cacheKey += ".gz";
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
    		CacheableRequestProcessor processor, 
    		boolean shouldZip, boolean zip) 
    		throws Exception {
    	
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
				TaggingContext ctx = TaggingContext.openNestedContext(request);
				//Map propertySnapshot = SharedProperties.getSnapshot(request);
				request = new SessionCreationPreventingRequestWrapper(request);
				processor.processRequest(request, wrapper);
				ctx.close();
				//Map props = SharedProperties.getDiff(request, propertySnapshot);
				//cacheItem.setProperties(props);
				cache.tagItem(cacheItem, ctx.getTags());
				wrapper.stopCapturing();
				if (wrapper.isOk() && !ctx.isPreventCaching()) {
					cacheItem.setLastModified(mtime);
				}
				else {
					cacheItem.invalidate();
				}
				if (cacheItem.getSize() > 0) {
					if (shouldZip) {
						wrapper.setHeader("Vary", "Accept-Encoding, User-Agent");
						if (zip) {
							cacheItem.gzipContent();
							wrapper.setHeader("Content-Encoding", "gzip");
						}
					}					
				}
				wrapper.updateHeaders();
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
   
    private boolean serve(CacheItem cacheItem, HttpServletRequest request, 
            HttpServletResponse response, SessionIdEncoder sessionIdEncoder) 
    		throws IOException {
    	
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
        	cacheItem.writeTo(response, sessionIdEncoder.getSessionId());
        	//SharedProperties.setProperties(request, cacheItem.getProperties());
    	}
    	finally {
    		lock.releaseReaderLock();
    	}
    	return true;
    }
    
    public boolean isCached(String key) {
    	return cache.containsKey(key);
    }
    
    /**
	 * Checks whether the response can be compressed. This is the case when
	 * {@link #clientAcceptsGzip(HttpServletRequest) the client accepts gzip 
	 * encoded content}, the {@link #userAgentHasGzipBugs(HttpServletRequest) 
	 * user-agent has no known gzip-related bugs} and the request is not an 
	 * {@link WebUtils#isIncludeRequest(javax.servlet.ServletRequest)
	 * include request}.
	 */
	protected boolean responseCanBeZipped(HttpServletRequest request) {
		return clientAcceptsGzip(request) 
				&& !userAgentHasGzipBugs(request)
				&& !WebUtils.isIncludeRequest(request);
	}
	
	/**
	 * Returns whether the Accept-Encoding header contains "gzip".
	 */
	protected boolean clientAcceptsGzip(HttpServletRequest request) {
		Enumeration values = request.getHeaders("Accept-Encoding");
		if (values != null) {
			while (values.hasMoreElements()) {
				String value = (String) values.nextElement();
				if (value.indexOf("gzip") != -1) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Returns whether the User-Agent has known gzip-related bugs. This is true
	 * for Internet Explorer &lt; 6.0 SP2 and Mozilla 4.06, 4.07 and 4.08. The
	 * method will also return true if the User-Agent header is not present or
	 * empty.
	 */
	protected boolean userAgentHasGzipBugs(HttpServletRequest request) {
		String ua = request.getHeader("User-Agent");
		if (!StringUtils.hasLength(ua)) {
			return true;
		}
		Matcher m = IE_MAJOR_VERSION_PATTERN.matcher(ua);
		if (m.find()) {
			int major = Integer.parseInt(m.group(1));
			if (major > 6) {
				// Bugs are fixed in IE 7 
				return false;
			}
			if (ua.indexOf("Opera") != -1) {
				// Opera has no known gzip bugs
				return false;
			}
			if (major == 6) {
				// Bugs are fixed in Service Pack 2 
				return ua.indexOf("SV1") == -1;
			}
			// All other version are buggy.
			return true;
		}
		return BUGGY_NETSCAPE_PATTERN.matcher(ua).find();
	}
	
	public void invalidateTaggedItems(String tag) {
		cache.invalidateTaggedItems(tag);
	}
}

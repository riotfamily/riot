package org.riotfamily.cachius.spring;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.cachius.CacheItem;
import org.springframework.web.servlet.mvc.Controller;


/**
 * Controllers that want their output to be cached must implement this
 * interface.
 */
public interface CacheableController extends Controller {
    
    public String getCacheKey(HttpServletRequest request);
    
    public long getLastModified(HttpServletRequest request, 
            CacheItem cacheItem) throws Exception;

}

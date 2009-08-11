package org.riotfamily.cachius.spring;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.servlet.ServletUtils;
import org.springframework.beans.factory.BeanNameAware;


/**
 * Abstract base class for cacheable controllers.
 * 
 * @author Felix Gnass
 */
public abstract class AbstractCacheableController 
		implements CacheableController, BeanNameAware {

	private String beanName;
	
	private boolean addUriToCacheKey = true;
	
	public final void setBeanName(String beanName) {
		this.beanName = beanName;
	}
		
	protected String getBeanName() {
		return beanName;
	}
	
	/**
	 * Sets whether the (originating) URI should be included in the cache-key.
	 * Set this property to <code>false</code> if the controller looks the same
	 * on every page, i.e. does not dependent on the URI. The default is 
	 * <code>true</code> which is safe (as it will rule out wrong content to 
	 * be displayed due to identical cache-keys) but could lead to many 
	 * identical cache items.
	 * @see #getCacheKeyInternal(HttpServletRequest)
	 */
	public void setAddUriToCacheKey(boolean addUriToCacheKey) {
		this.addUriToCacheKey = addUriToCacheKey;
	}
	
	/**
     * Returns whether the cache should be bypassed. The default implementation 
     * always returns <code>false</code>. 
     */
    protected boolean bypassCache(HttpServletRequest request) {
		return false;
	}
    
	/**
	 * Returns the cache-key for the request. The call is delegated to
	 * {@link #getCacheKeyInternal(HttpServletRequest)}, unless 
	 * {@link #bypassCache(HttpServletRequest)} returns <code>true</code>.
	 */
    public final String getCacheKey(HttpServletRequest request) {
    	if (bypassCache(request)) {
    		return null;
    	}
    	return getCacheKeyInternal(request);
    }
    
    /**
     * Returns the actual cache-key. Invoked by
     * {@link #getCacheKey(HttpServletRequest) getCacheKey()}
     * if {@link #bypassCache(HttpServletRequest) bypassCache()} 
     * returned <code>false</code>.
     * <p>
     * The method creates a StringBuffer containing either the bean-name or the 
     * {@link ServletUtils#getOriginatingPathWithinApplication(HttpServletRequest) 
     * originating path} and, in case of an include or forward, the 
     * {@link ServletUtils#getPathWithinApplication(HttpServletRequest) nested path}.
     * <p> 
     * The StringBuffer is passed to {@link #appendCacheKey(StringBuffer, HttpServletRequest)},
     * allowing subclasses to add additional information.
     */
    protected String getCacheKeyInternal(HttpServletRequest request) {
    	StringBuffer key;
    	if (addUriToCacheKey) {
    		key = request.getRequestURL();
			if (!ServletUtils.isDirectRequest(request)) {
				key.append('#').append(ServletUtils.getPathWithinApplication(request));
			}
    	}
    	else {
    		key = new StringBuffer(getBeanName());
    	}
		appendCacheKey(key, request);
		return key.toString();
    }
            
    /**
     * Subclasses may overwrite this method to append values to the cache-key.
     * The default implementation does nothing.
     * @see #getCacheKeyInternal(HttpServletRequest)
     */
	protected void appendCacheKey(StringBuffer key, HttpServletRequest request) {
	}

	/**
	 * The default implementation returns <code>0</code> so that 
	 * {@link #getLastModified(HttpServletRequest)} is invoked every time the
	 * controller is requested.
	 */
	public long getTimeToLive() {
		return 0;
	}
	
    /**
     * The default implementation returns 
     * <code>System.currentTimeMillis()</code> so that the item is 
     * refreshed as soon as it expires. Subclasses should override this
     * method to return something reasonable.
     */
    public long getLastModified(HttpServletRequest request) {
        return System.currentTimeMillis();
    }
    
}

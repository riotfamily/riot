package org.riotfamily.cachius;

import java.io.Serializable;

import org.riotfamily.cachius.persistence.DiskStore;

public interface CacheHandler {

	/**
	 * Returns the key that is used to look up a previously cached version.
	 * The key must include all values that govern the output. The key itself 
	 * is not interpreted in any way and thus can have an arbitrary format.
	 */
	public String getCacheKey();
	
	/**
	 * Returns the name of the cache region to use. Implementors may return
	 * <code>null</code> in order to use the default region. 
	 */
	public String getCacheRegion();
		
	/**
     * Returns the date (as timestamp) when the content was modified for the 
     * last time. The {@link #capture} method will not be called unless this 
     * date is newer than the timestamp of the cached version.
     */
	public long getLastModified();
	
	public Serializable capture(DiskStore diskStore) throws Exception;
	
	public void serve(Serializable data) throws Exception;
	
	/**
	 * Callback method that is invoked when no CacheItem could be created. 
	 * This can be the case when either {@link #getCacheKey()} returns 
	 * <code>null</code>, the cache is disabled or an unexpected error occurred. 
	 */
	public void handleUncached() throws Exception;

}

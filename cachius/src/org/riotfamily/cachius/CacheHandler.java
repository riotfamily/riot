/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.cachius;

import java.io.IOException;

/**
 * Callback interface that can be passed to 
 * {@link CacheService#handle(CacheHandler)}.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public interface CacheHandler {

	public static final long CACHE_ETERNALLY = -1L;
	
	/**
	 * Returns the key that is used to look up a previously cached version.
	 * The key must include all values that govern the output. The key itself 
	 * is not interpreted in any way and thus can have an arbitrary format.
	 */
	public String getCacheKey();
	
	/**
     * Returns the time in milliseconds that has to be elapsed since the last
     * up-to-date check before another check is performed. Implementors may 
     * return {@link #CACHE_ETERNALLY} to indicate that the content should be 
     * cached eternally.
     */
	public long getTimeToLive();
	
	/**
     * Returns the date (as timestamp) when the content was modified for the 
     * last time. The {@link #updateCacheItem(CacheItem)} method will not be 
     * called unless this date is newer than the timestamp of the cached 
     * version.
     */
	public long getLastModified() throws Exception;
	
	/**
	 * Callback method that is invoked when no CacheItem could be created. 
	 * This can be the case when either {@link #getCacheKey()} returns 
	 * <code>null</code>, the cache is disabled or an unexpected error occurred. 
	 */
	public void handleUncached() throws Exception;

	/**
	 * Callback method that is invoked when no cached version exists or the 
	 * cached content is not up-to-date. Implementors must obtain a Writer
	 * or an OutputStram by calling {@link CacheItem#getWriter()} or
	 * {@link CacheItem#getOutputStream()} and write their content into it.
	 * @return <code>true</code> if the CacheItem should be updated or 
	 * <code>false</code> if it should be discarded.  
	 */
	public boolean updateCacheItem(CacheItem cacheItem) throws Exception;
	
	/**
	 * Callback method that is invoked when an up-to-date CacheItem exists.
	 * Implementors must invoke one of the <code>CacheItem.writeTo(...)</code>
	 * to write the cached content to its destination.
	 */
	public void writeCacheItem(CacheItem cacheItem)	throws IOException;
	
}

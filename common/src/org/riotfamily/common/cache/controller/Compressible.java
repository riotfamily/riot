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
package org.riotfamily.common.cache.controller;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.cachius.CacheService;

/**
 * Interface that can be implemented by controllers that deliver compressible
 * content.
 * 
 * @see <a href="http://developer.yahoo.com/performance/rules.html#gzip">Best Practices for Speeding Up Your Web Site</a>
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public interface Compressible {

	/**
	 * Returns whether the response should be gzipped. Implementors will 
	 * usually return <code>true</code>, unless they serve multiple content
	 * types which and not all of them are eligible for compression. They 
	 * <strong>don't have to</strong> check whether the client supports gzip 
	 * compression as all compatibility checks are done by Cachius 
	 * {@link CacheService#responseCanBeZipped(HttpServletRequest) internally}. 
	 */
	public boolean gzipResponse(HttpServletRequest request);

}

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
package org.riotfamily.cachius.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

/**
 * HttpServletRequestWrapper that prevents the creation of new HTTP sessions.
 * An IllegalStateException is thrown when 
 * {@link #getSession()} or {@link #getSession(boolean) getSession(true)} is
 * invoked and no session existed at the time the wrapper was created.
 * <p>
 * <strong>Rationale:</strong> Cachius supports the caching of snippets that
 * use URL rewriting for session tracking. It therefore stores two versions
 * (one with rewritten URLs and one without) under different cache keys.
 * Hence Cachius must know whether URL rewriting is used <em>before</em> a
 * CacheItem is retrieved or created. This implies that cachable snippets must
 * not create new sessions.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class SessionCreationPreventingRequestWrapper 
		extends HttpServletRequestWrapper {
	
	private boolean sessionExists;
	
	public SessionCreationPreventingRequestWrapper(HttpServletRequest request) {
		super(request);
		sessionExists = request.getSession(false) != null;
	}
	
	@Override
	public HttpSession getSession() {
		return getSession(true);
	}
	
	@Override
	public HttpSession getSession(boolean create) {
		if (create && !sessionExists) {
			throw new IllegalStateException("CacheableControllers must not " +
					"create new HTTP sessions. Make sure that the session " +
					"exists before you invoke the Controller.");
		}
		return super.getSession(create);
	}

}

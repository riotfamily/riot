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
package org.riotfamily.core.security.session;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.filter.FilterPlugin;
import org.riotfamily.core.security.auth.RiotUser;

/**
 * Servlet filter that binds the authenticated user (if present) to the
 * current thread. 
 * 
 * @see LoginManager#getUser(HttpServletRequest)
 * @see SecurityContext#bindUserToCurrentThread(RiotUser)
 */
public final class AccessControlFilterPlugin extends FilterPlugin {

	public AccessControlFilterPlugin() {
		setOrder(0);
	}

	public void doFilter(HttpServletRequest request,
		HttpServletResponse response, FilterChain filterChain)
		throws IOException, ServletException {
		
		try {
			LoginManager loginManager = LoginManager.getInstance(getServletContext());
			RiotUser user = loginManager.getUser(request);
			SecurityContext.bindUserToCurrentThread(user);
			filterChain.doFilter(request, response);
		}
		finally {
			SecurityContext.resetUser();
		}
	}
}

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

import org.riotfamily.common.web.filter.DiagnosticContextFilter;
import org.riotfamily.core.security.auth.RiotUser;
import org.slf4j.MDC;

/**
 * Class that associates a RiotUser with the current thread.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class SecurityContext {

	private static ThreadLocal<RiotUser> threadLocal = new ThreadLocal<RiotUser>();
	
	public static void bindUserToCurrentThread(RiotUser user) {
		threadLocal.set(user);
		if (user != null) {
			MDC.put("RiotUser", user.getUserId());
		}
	}
	
	public static RiotUser getCurrentUser() {
		return threadLocal.get();
	}
	
	public static void resetUser() {
		threadLocal.set(null);
		if (!DiagnosticContextFilter.isPresent()) {
			MDC.remove("RiotUser");
		}
	}
	
}

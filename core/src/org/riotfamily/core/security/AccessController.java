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
package org.riotfamily.core.security;

import java.util.List;

import org.riotfamily.core.security.auth.RiotUser;
import org.riotfamily.core.security.policy.AssertionPolicy;
import org.riotfamily.core.security.policy.AuthorizationPolicy;
import org.riotfamily.core.security.policy.PermissionDeniedException;
import org.riotfamily.core.security.policy.AuthorizationPolicy.Permission;
import org.riotfamily.core.security.session.AccessControlFilterPlugin;
import org.riotfamily.core.security.session.SecurityContext;



/**
 * Provides static methods to check permissions and associate a user
 * with the current Thread. 
 * <p>
 * This class is only usable if an {@link AccessControlFilterPlugin} or 
 * {@link AccessControlInterceptor} is configured.  
 */
public final class AccessController {

	private AccessController() {
	}
	
	private static List<AuthorizationPolicy> policies;

	/**
	 * The {@link AccessControlInitializer} sets a list of 
	 * {@link AuthorizationPolicy policies} so that they can be accessed 
	 * from a static context.
	 */
	static void setPolicies(List<AuthorizationPolicy> policies) {
		AccessController.policies = policies;
	}
		
	@SuppressWarnings("unchecked")
	public static <T extends RiotUser> T getCurrentUser() {
		return (T) SecurityContext.getCurrentUser();
	}
	
	public static boolean isAuthenticatedUser() {
		return getCurrentUser() != null;
	}
	
	public static boolean isGranted(String action, Object object) {
		return isGranted(getCurrentUser(), action, object);
	}
	
	public static boolean isGranted(String action, Object... object) {
		return isGranted(getCurrentUser(), action, object);
	}
	
	public static boolean isGranted(RiotUser user, String action, Object object) {
		if (user != null) {
			for (AuthorizationPolicy policy : policies) {
				Permission permission = policy.getPermission(user, action, object);
				if (permission == Permission.GRANTED) {
					return true;
				}
				else if (permission == Permission.DENIED) {
					return false;
				}
			}
		}
		return false;
	}
	
	public static void assertIsGranted(String action, Object object) 
			throws PermissionDeniedException {
		
		RiotUser subject = getCurrentUser();
		if (subject != null) {
			for (AuthorizationPolicy policy : policies) {
				if (policy instanceof AssertionPolicy) {
					AssertionPolicy assertionPolicy = (AssertionPolicy) policy;
					assertionPolicy.assertIsGranted(subject, action, object);
				}
				else if (policy.getPermission(subject, action, object) == Permission.DENIED) {
					throw new PermissionDeniedException(subject, action, object, policy);
				}
			}
		}
	}
	
}

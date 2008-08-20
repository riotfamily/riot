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
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.security;

import java.util.List;

import org.riotfamily.riot.security.auth.RiotUser;
import org.riotfamily.riot.security.policy.AuthorizationPolicy;
import org.riotfamily.riot.security.session.AccessControlFilterPlugin;
import org.riotfamily.riot.security.session.AccessControlInterceptor;
import org.riotfamily.riot.security.session.SecurityContext;



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
		
	public static RiotUser getCurrentUser() {
		return SecurityContext.getCurrentUser();
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
	
	public static void checkPermission(String action, Object object) {
		RiotUser subject = getCurrentUser();
		if (subject != null) {
			for (AuthorizationPolicy policy : policies) {
				int access = policy.checkPermission(subject, action, object);
				if (access == AuthorizationPolicy.ACCESS_GRANTED) {
					return;
				}
				else if (access == AuthorizationPolicy.ACCESS_DENIED) {
					throw new AccessDeniedException(subject, action, object, policy);
				}
			}
		}
		throw new AccessDeniedException(subject, action, object, null);
	}

	public static boolean isGranted(RiotUser user, String action, Object object) {
		if (user != null) {
			for (AuthorizationPolicy policy : policies) {
				int access = policy.checkPermission(user, action, object);
				if (access == AuthorizationPolicy.ACCESS_GRANTED) {
					return true;
				}
				else if (access == AuthorizationPolicy.ACCESS_DENIED) {
					return false;
				}
			}
		}
		return false;
	}
	
}

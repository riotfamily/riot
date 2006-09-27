package org.riotfamily.riot.security;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.riot.editor.EditorDefinition;
import org.riotfamily.riot.security.policy.AuthorizationPolicy;


/**
 * Provides static methods to check permissions and associate a principal
 * with the current Thread. 
 * 
 * A {@link org.riotfamily.riot.security.PrincipalBinder PrincipalBinder} and a 
 * {@link org.riotfamily.riot.security.policy.AuthorizationPolicy policy} 
 * must be set. This can be donew using the {@link 
 * org.riotfamily.riot.security.AccessControlConfigurer AccessControlConfigurer}.
 * 
 * This class is only usable if an 
 * @link org.riotfamily.riot.security.AccessControlFilter or
 * @link org.riotfamily.riot.security.AccessControlInterceptor is configured.  
 */
public final class AccessController {

	private AccessController() {
	}
	
	private static PrincipalBinder principalBinder;
	
	private static List policies;
	
	private static ThreadLocal principal = new ThreadLocal();
	
	
	public static void setPrincipalBinder(PrincipalBinder loginManager) {
		AccessController.principalBinder = loginManager;
	}

	public static void setPolicies(List policies) {
		AccessController.policies = policies;
	}

	static public String getPrincipal(HttpServletRequest request) {
		return principalBinder.getPrincipal(request);
	}
	
	public static String getPrincipalForCurrentThread() {
		return (String) principal.get();
	}
	
	public static boolean isAuthenticatedUser() {
		return getPrincipalForCurrentThread() != null;
	}
	
	static void bindPrincipalToCurrentThread(String s) {
		principal.set(s);
	}
	
	static void bindPrincipalToCurrentTread(HttpServletRequest request) {
		principal.set(principalBinder.getPrincipal(request));
	}
	
	static void resetPrincipal() {
		principal.set(null);
	}
	
	public static boolean isGranted(String action, Object object, 
			EditorDefinition editor) {
		
		String principal = getPrincipalForCurrentThread();
		Iterator it = policies.iterator();
		while (it.hasNext()) {
			AuthorizationPolicy policy = (AuthorizationPolicy) it.next();
			int access = policy.checkPermission(principal, action, object, editor);
			if (access == AuthorizationPolicy.ACCESS_GRANTED) {
				return true;
			}
			else if (access == AuthorizationPolicy.ACCESS_DENIED) {
				return false;
			}
		}
		return false;
	}	
}

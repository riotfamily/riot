package org.riotfamily.riot.security;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.riot.editor.EditorDefinition;


/**
 * Provides static methods to check permissions and associate a principal
 * with the current Thread. 
 * 
 * This class is only usable if an 
 * @link org.riotfamily.riot.security.AccessControlFilter or
 * @link org.riotfamily.riot.security.AccessControlInterceptor is configured.  
 */
public final class AccessController {

	private AccessController() {
	}
	
	private static LoginManager loginManager;
	
	private static ThreadLocal principal = new ThreadLocal();
	
	
		public static void setLoginManager(LoginManager loginManager) {
		AccessController.loginManager = loginManager;
	}

	static public String getPrincipal(HttpServletRequest request) {
		return loginManager.getPrincipal(request);
	}
	
	static void bindPrincipalToCurrentThread(String s) {
		principal.set(s);
	}
	
	static void bindPrincipalToCurrentTread(HttpServletRequest request) {
		principal.set(getPrincipal(request));
	}
	
	public static String getPrincipalForCurrentThread() {
		return (String) principal.get();
	}
	
	static void resetPrincipal() {
		principal.set(null);
	}
	
	public static boolean isAuthenticatedUser() {
		return getPrincipalForCurrentThread() != null;
	}
	
	public static boolean isGranted(String action, Object object, 
			EditorDefinition editor) {
		
		return loginManager.isGranted(getPrincipalForCurrentThread(), 
				action, object, editor);
	}	
}

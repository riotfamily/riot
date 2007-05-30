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

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.riot.editor.EditorDefinition;


/**
 * Provides static methods to check permissions and associate a principal
 * with the current Thread. 
 * <p>
 * This class is only usable if an {@link AccessControlFilterPlugin} or 
 * {@link AccessControlInterceptor} is configured.  
 */
public final class AccessController {

	private AccessController() {
	}
	
	private static LoginManager loginManager;
	
	private static ThreadLocal principal = new ThreadLocal();
	
	
	static void setLoginManager(LoginManager loginManager) {
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
	
	public static boolean isGranted(String action, Object object) {
		
		return loginManager.isGranted(getPrincipalForCurrentThread(), 
				action, object, null);
	}	
}

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.riot.editor.EditorDefinition;
import org.riotfamily.riot.security.policy.AuthorizationPolicy;
import org.riotfamily.riot.security.session.SessionData;
import org.riotfamily.riot.security.session.SessionDataStore;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.OrderComparator;

public class LoginManager implements ApplicationContextAware, InitializingBean {

	public static final String ACTION_LOGIN = "login";
	
	private AuthenticationService authService;
	
	private PrincipalBinder principalBinder;
	
	private SessionDataStore sessionDataStore; 
	
	private ArrayList policies;
	
	public LoginManager(AuthenticationService authService, 
			PrincipalBinder binder) {
		
		this.authService = authService;
		this.principalBinder = binder;
	}
	
	public void setApplicationContext(ApplicationContext context) {
		policies = new ArrayList();
		
		policies.addAll(BeanFactoryUtils.beansOfTypeIncludingAncestors(
			context, AuthorizationPolicy.class).values());
		
		Collections.sort(policies, new OrderComparator());
	}
	
	public void afterPropertiesSet() throws Exception {
		AccessController.setLoginManager(this);	
	}

	public void setSessionDataStore(SessionDataStore sessionDataStore) {
		this.sessionDataStore = sessionDataStore;
	}

	public boolean login(HttpServletRequest request, String username, 
			String password) {
		
		String principal = authService.authenticate(username, password);
		if (principal != null && isGranted(principal, ACTION_LOGIN, null, null)) {
			principalBinder.bindPrincipalToRequest(principal, request);
			SessionData sessionData = null;
			if (sessionDataStore != null) {
				sessionData = sessionDataStore.loadSessionData(principal);
			}
			if (sessionData == null) {
				sessionData = new SessionData();
			}
			
			sessionData.setUsername(username);
			sessionData.setPrincipal(principal);
			sessionData.newSession(request, sessionDataStore);
			
			return true;
		}
		return false;
	}
	
	public void logout(HttpServletRequest request, 
			HttpServletResponse response) {
		
		principalBinder.unbind(request, response);
		request.getSession().invalidate();
	}
	
	public String getPrincipal(HttpServletRequest request) {
		return principalBinder.getPrincipal(request);
	}
	
	public boolean isGranted(String subject, String action, Object object, 
			EditorDefinition editor) {
		
		Iterator it = policies.iterator();
		while (it.hasNext()) {
			AuthorizationPolicy policy = (AuthorizationPolicy) it.next();
			int access = policy.checkPermission(subject, action, object, editor);
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

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
package org.riotfamily.riot.security.session;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.riot.security.auth.AuthenticationService;
import org.riotfamily.riot.security.auth.RiotUser;
import org.springframework.web.context.ServletContextAware;

public class LoginManager implements ServletContextAware {

	private static final String CONTEXT_KEY = LoginManager.class.getName();
		
	private AuthenticationService authenticationService;

	private SessionMetaDataStore metaDataStore; 
	
	
	public LoginManager(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}
	
	public void setMetaDataStore(SessionMetaDataStore metaDataStore) {
		this.metaDataStore = metaDataStore;
	}

	public void setServletContext(ServletContext servletContext) {
		servletContext.setAttribute(CONTEXT_KEY, this);
	}
	
	public static LoginManager getInstance(ServletContext servletContext) {
		return (LoginManager) servletContext.getAttribute(CONTEXT_KEY);
	}
	
	/**
	 * Tries to authenticate the user with the given credentials. If the 
	 * authentication succeeds the RiotUser object is stored in the 
	 * HTTP session.
	 */
	public boolean login(HttpServletRequest request, String userName, 
			String password) {
		
		RiotUser user = authenticationService.authenticate(userName, password);
		if (user != null) {
			storeUserInSession(userName, user, request);
			return true;
		}
		return false;
	}
	
	/**
	 * Performs a logout. This is done by removing the {@link UserHolder} 
	 * object from the session. 
	 */
	public static void logout(HttpServletRequest request, 
			HttpServletResponse response) {
		
		UserHolder.removeFromSession(request);
	}
	
	/**
	 * Retrieves the {@link SessionMetaData} for the given user from the 
	 * {@link SessionMetaDataStore}. If no store is configured or no persistent
	 * data is found, a new instance is created. 
	 */
	private SessionMetaData getOrCreateMetaData(String userName, RiotUser user, 
			HttpServletRequest request) {
		
		SessionMetaData metaData = null;
		if (metaDataStore != null) {
			metaData = metaDataStore.loadSessionMetaData(user);
		}
		if (metaData == null) {
			metaData = new SessionMetaData(user.getUserId());
		}
		metaData.sessionStarted(userName, request.getRemoteHost());
		return metaData;
	}
	
	/**
	 * Stores the given SessionData in the {@link SessionMetaDataStore}.
	 */
	void storeSessionMetaData(SessionMetaData sessionData) {
		if (metaDataStore != null) {
			metaDataStore.storeSessionMetaData(sessionData);
		}
	}
	
	/**
	 * Stores the given user in the HTTP session. Actually a {@link UserHolder}
	 * object is used, that holds both, the RiotUser and the SessionData. 
	 */
	private void storeUserInSession(String userName, RiotUser user, 
			HttpServletRequest request) {
		
		SessionMetaData sessionData = getOrCreateMetaData(userName, user, request);
		UserHolder holder = new UserHolder(user, sessionData);
		holder.storeInSession(request.getSession());
	}

	/**
	 * Returns the user associated with the given request.
	 */
	static RiotUser getUser(HttpServletRequest request) {
		UserHolder holder = UserHolder.getInstance(request);
		return holder != null ? holder.getUser() : null;
	}

	/**
	 * Returns the SessionData for the given request.
	 */
	public static SessionMetaData getSessionMetaData(HttpServletRequest request) {
		UserHolder holder = UserHolder.getInstance(request);
		return holder != null ? holder.getSessionMetaData() : null;
	}
	
}

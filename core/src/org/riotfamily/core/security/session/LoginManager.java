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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.riotfamily.core.security.auth.AuthenticationService;
import org.riotfamily.core.security.auth.RiotUser;
import org.riotfamily.core.security.auth.UserLookupAuthenticationService;
import org.springframework.web.context.ServletContextAware;

public class LoginManager implements ServletContextAware {

	private static final String CONTEXT_KEY = LoginManager.class.getName();
		
	private AuthenticationService authenticationService;

	private UserLookupAuthenticationService userLookupService;
	
	private SessionMetaDataStore metaDataStore = new TransientSessionMetaDataStore();
	
	
	public LoginManager(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
		if (authenticationService instanceof UserLookupAuthenticationService) {
			userLookupService = (UserLookupAuthenticationService) authenticationService;
		}
	}
	
	public void setMetaDataStore(SessionMetaDataStore metaDataStore) {
		this.metaDataStore = metaDataStore;
	}

	/**
	 * Stores a reference to itself in the ServletContext.
	 * @see ServletContextAware
	 */
	public void setServletContext(ServletContext servletContext) {
		servletContext.setAttribute(CONTEXT_KEY, this);
	}
	
	/**
	 * Returns the LoginManager for the given ServletContext.
	 */
	public static LoginManager getInstance(ServletContext servletContext) {
		return (LoginManager) servletContext.getAttribute(CONTEXT_KEY);
	}
		
	/**
	 * Tries to authenticate the user with the given credentials. If the 
	 * authentication succeeds the RiotUser is stored in the HTTP session.
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
	 * Performs a logout by invalidating the session. 
	 */
	public static void logout(HttpServletRequest request, 
			HttpServletResponse response) {
		
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
	}
	
	/**
	 * Retrieves the {@link SessionMetaData} for the given user from the 
	 * {@link SessionMetaDataStore}. If no store is configured or no persistent
	 * data is found, a new instance is created. 
	 */
	private SessionMetaData sessionStarted(String userName, RiotUser user, 
			HttpServletRequest request) {
		
		return metaDataStore.sessionStarted(userName, user, request.getRemoteHost());
	}
	
	/**
	 * Stores the given SessionData in the {@link SessionMetaDataStore}.
	 */
	void sessionEnded(SessionMetaData sessionData) {
		metaDataStore.sessionEnded(sessionData);
	}
	
	/**
	 * Stores the given user in the HTTP session. Actually a {@link UserHolder}
	 * object is used, that holds both, the RiotUser and the SessionData. 
	 */
	private void storeUserInSession(String userName, RiotUser user, 
			HttpServletRequest request) {
		
		SessionMetaData sessionData = sessionStarted(userName, user, request);
		UserHolder.storeInSession(user, sessionData, request.getSession());
	}

	/**
	 * Returns the user associated with the given request.
	 */
	protected RiotUser getUser(HttpServletRequest request) {
		UserHolder holder = UserHolder.getInstance(request);
		if (holder != null) {
			RiotUser user = holder.getUser();
			if (user == null) {
				user = holder.reloadUser(userLookupService);
			}
			return user;
		}
		return null;
	}

	/**
	 * Returns the SessionData for the given request.
	 */
	public static SessionMetaData getSessionMetaData(HttpServletRequest request) {
		UserHolder holder = UserHolder.getInstance(request);
		return holder != null ? holder.getSessionMetaData() : null;
	}
	
}

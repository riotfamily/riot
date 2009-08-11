package org.riotfamily.core.security.session;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.riotfamily.core.security.auth.RiotUser;

/**
 * SessionMetaDataStore implementation that doesn't store anything.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class TransientSessionMetaDataStore implements SessionMetaDataStore {

	public List<SessionMetaData> listAll() {
		return Collections.emptyList();
	}
	
	public SessionMetaData sessionStarted(String userName, RiotUser user, String loginIP) {
		return new SessionMetaDataImpl(userName, user);
	}

	public void sessionEnded(SessionMetaData sessionData) {
	}
	
	private static class SessionMetaDataImpl implements SessionMetaData {

		private String userName;
		
		private String userId;
		
		private SessionMetaDataImpl(String userName, RiotUser user) {
			this.userName = userName;
			this.userId = user.getUserId();
		}
		
		public Date getLastLoginDate() {
			return null;
		}

		public String getLastLoginIP() {
			return null;
		}

		public String getUserId() {
			return userId;
		}

		public String getUserName() {
			return userName;
		}
		
	}
	
}

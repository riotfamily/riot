package org.riotfamily.riot.security.support;

import org.riotfamily.riot.security.session.SessionData;
import org.riotfamily.riot.security.session.SessionDataStore;

public class DefaultSessionDataStore implements SessionDataStore {

	public SessionData loadSessionData(String principal) {
		return null;
	}

	public void storeSessionData(SessionData sessionData) {
	}
	
}

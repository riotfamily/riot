package org.riotfamily.riot.security.session;

public interface SessionDataStore {

	public SessionData loadSessionData(String principal);
	
	public void storeSessionData(SessionData sessionData);
}

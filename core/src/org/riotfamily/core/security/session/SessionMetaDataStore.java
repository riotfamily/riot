package org.riotfamily.core.security.session;

import java.util.List;

import org.riotfamily.core.security.auth.RiotUser;

/**
 * Interface to load and persist {@link SessionMetaData}.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public interface SessionMetaDataStore<T extends SessionMetaData> {

	public List<T> listAll();
	
	public T sessionStarted(String userName, RiotUser user, String loginIP);
	
	public void sessionEnded(T data);

}

package org.riotfamily.core.security.auth;

import org.riotfamily.core.dao.RiotDao;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public interface RiotUserDao extends RiotDao {

	public RiotUser findUserByCredentials(String username, String password);
	
	public RiotUser findUserById(String userId);
	
	public void updatePassword(RiotUser user, String newPassword);
	
}

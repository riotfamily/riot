package org.riotfamily.core.security.auth;

import org.springframework.transaction.annotation.Transactional;


/**
 * AuthenticationService that uses a {@link RiotUserDao} to lookup a 
 * {@link RiotUser}.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class UserDaoAuthenticationService 
		implements UserLookupAuthenticationService {

	private RiotUserDao userDao;
	
	public void setUserDao(RiotUserDao userDao) {
		this.userDao = userDao;
	}

	@Transactional
	public RiotUser authenticate(String username, String password) {
		return userDao.findUserByCredentials(username, password);
	}
	
	@Transactional
	public RiotUser getUserById(String userId) {
		return userDao.findUserById(userId);
	}
	
}

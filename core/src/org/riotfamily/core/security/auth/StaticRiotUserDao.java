package org.riotfamily.core.security.auth;

import org.riotfamily.core.dao.RiotDaoAdapter;
import org.springframework.beans.factory.annotation.Required;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class StaticRiotUserDao extends RiotDaoAdapter implements RiotUserDao {

	private static final RiotUser ROOT = new RootUser();
	
	private String username;
	
	private String password;

	@Required
	public void setUsername(String username) {
		this.username = username;
	}
	
	@Required
	public void setPassword(String password) {
		this.password = password;
	}

	public RiotUser findUserByCredentials(String username, String password) {
		if (this.username.equals(username) && this.password.equals(password)) {
			return ROOT;
		}
		return null;
	}
	
	public RiotUser findUserById(String userId) {
		if (RootUser.ID.equals(userId)) {
			return ROOT;
		}
		return null;
	}
	
	public void updatePassword(RiotUser user, String newPassword) {
		password = newPassword;		
	}
	
	private static class RootUser implements RiotUser {

		private static final String ID = "root";
		
		public String getUserId() {
			return ID;
		}
		
		public String getEmail() {
			return null;
		}
		
		public String getName() {
			return ID;
		}
	
	}

}
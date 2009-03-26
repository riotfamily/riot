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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
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
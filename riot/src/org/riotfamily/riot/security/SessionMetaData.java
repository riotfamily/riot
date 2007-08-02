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
package org.riotfamily.riot.security;

import java.io.Serializable;
import java.util.Date;

/**
 * Class that holds meta data about the last and current session.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class SessionMetaData implements Serializable {

	private String userId;
	
	private String userName;
	
	private Date loginDate;
	
	private Date lastLoginDate;
	
	private String loginIP;
	
	private String lastLoginIP;

	
	public SessionMetaData() {
	}

	public SessionMetaData(String userId, String userName, String loginIP) {
		this.userId = userId;
		this.userName = userName;
		this.loginIP = loginIP;
		this.loginDate = new Date();
	}
	
	protected void sessionEnded() {
		lastLoginDate = loginDate;
		lastLoginIP = loginIP;
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	public String getLastLoginIP() {
		return lastLoginIP;
	}

	public void setLastLoginIP(String lastLoginIP) {
		this.lastLoginIP = lastLoginIP;
	}
	
}

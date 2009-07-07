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
package org.riotfamily.riot.hibernate.security;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.riotfamily.common.hibernate.ActiveRecord;
import org.riotfamily.core.security.auth.RiotUser;
import org.riotfamily.core.security.session.SessionMetaData;

@Entity
@Table(name="riot_session_data")
public class PersistentSessionMetaData extends ActiveRecord 
		implements SessionMetaData {

	private String userId;
	
	private String userName;
	
	private Date loginDate;
	
	private Date lastLoginDate;
	
	private String loginIP;
	
	private String lastLoginIP;

	public PersistentSessionMetaData() {
	}
	
	public PersistentSessionMetaData(RiotUser user) {
		this.userId = user.getUserId();
	}

	@Id
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

	public Date getLoginDate() {
		return loginDate;
	}

	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	public String getLoginIP() {
		return loginIP;
	}

	public void setLoginIP(String loginIP) {
		this.loginIP = loginIP;
	}

	public String getLastLoginIP() {
		return lastLoginIP;
	}

	public void setLastLoginIP(String lastLoginIP) {
		this.lastLoginIP = lastLoginIP;
	}
	
	// ----------------------------------------------------------------------
	
	void sessionStarted(String userName, String loginIP) {
		this.userName = userName;
		this.loginIP = loginIP;
		this.loginDate = new Date();
	}
	
	void sessionEnded() {
		lastLoginDate = loginDate;
		lastLoginIP = loginIP;
	}
	
	// ----------------------------------------------------------------------
	
	public static List<PersistentSessionMetaData> findAll() {
		return find("from PersistentSessionMetaData");
	}
	
	public static PersistentSessionMetaData loadByUser(RiotUser user) {
		return load(PersistentSessionMetaData.class, user.getUserId());
	}
	
}

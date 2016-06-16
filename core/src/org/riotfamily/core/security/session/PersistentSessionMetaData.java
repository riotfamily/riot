/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.core.security.session;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.riotfamily.common.hibernate.ActiveRecord;
import org.riotfamily.core.security.auth.RiotUser;

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
		return query(PersistentSessionMetaData.class, "from {}").find();
	}
	
	public static PersistentSessionMetaData loadByUser(RiotUser user) {
		return query(PersistentSessionMetaData.class,
				"from {} where userId = ?1", user.getUserId())
				.load();
	}
	
}

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
package org.riotfamily.riot.security.session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.riotfamily.riot.security.AccessController;
import org.riotfamily.riot.security.auth.RiotUser;

/**
 * Class that holds a reference to a RiotUser. An instance of this class is
 * stored in the HttpSession. Additionally each instance is placed in a static
 * list which allows us to access/update all currently logged in users. 
 * <p>
 * The class also implements the HttpSessionBindingListener interface and
 * persists the SessionMetaData as soon as the session expires.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class UserHolder implements Serializable, HttpSessionBindingListener {

	private static ArrayList users = new ArrayList();
	
	private RiotUser user;
	
	private SessionMetaData metaData;
	
	
	public UserHolder(RiotUser user, SessionMetaData metaData) {
		this.user = user;
		this.metaData = metaData;
	}
	
	public RiotUser getUser() {
		return this.user;
	}

	public void setUser(RiotUser user) {
		this.user = user;
	}	
	
	public SessionMetaData getSessionMetaData() {
		return this.metaData;
	}
	
	public void valueBound(HttpSessionBindingEvent event) {
		users.add(this);
	}
	
	public void valueUnbound(HttpSessionBindingEvent event) {
		users.remove(user.getUserId());
		metaData.sessionEnded();
		AccessController.storeSessionMetaData(metaData);
	}
	
	public static void updateUser(String userId, RiotUser user) {
		Iterator it = users.iterator();		
		while (it.hasNext()) {
			UserHolder holder = (UserHolder) it.next();
			if (holder.getUser() != null 
					&& userId.equals(holder.getUser().getUserId())) {
				
				holder.setUser(user);
			}
		}
	}
	
}

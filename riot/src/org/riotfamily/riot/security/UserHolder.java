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
package org.riotfamily.riot.security;

import java.io.Serializable;
import java.util.HashMap;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class UserHolder implements Serializable, HttpSessionBindingListener {

	private static HashMap users = new HashMap();
	
	private RiotUser user;
	
	private SessionMetaData metaData;
	
	
	public UserHolder(RiotUser user, SessionMetaData metaData) {
		this.user = user;
		this.metaData = metaData;
	}
	
	public RiotUser getUser() {
		return this.user;
	}
	
	public SessionMetaData getSessionMetaData() {
		return this.metaData;
	}
	
	public void valueBound(HttpSessionBindingEvent event) {
		users.put(user.getUserId(), this);
	}
	
	public void valueUnbound(HttpSessionBindingEvent event) {
		users.remove(user.getUserId());
		metaData.sessionEnded();
		AccessController.storeSessionMetaData(metaData);
	}
}

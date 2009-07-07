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
package org.riotfamily.riot.hibernate.security;

import java.util.List;

import org.riotfamily.core.security.auth.RiotUser;
import org.riotfamily.core.security.session.SessionMetaDataStore;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PersistentSessionMetaDataStore 
		implements SessionMetaDataStore<PersistentSessionMetaData> {

	public List<PersistentSessionMetaData> listAll() {
		return PersistentSessionMetaData.findAll();
	}

	public void sessionEnded(PersistentSessionMetaData data) {
		data = data.merge();
		data.sessionEnded();
	}

	public PersistentSessionMetaData sessionStarted(String userName, RiotUser user, 
			String loginIP) {
		
		PersistentSessionMetaData meta = PersistentSessionMetaData.loadByUser(user); 
		if (meta == null) {
			meta = new PersistentSessionMetaData(user);
			meta.save();
		}
		meta.sessionStarted(userName, loginIP);
		return meta;
	}

}

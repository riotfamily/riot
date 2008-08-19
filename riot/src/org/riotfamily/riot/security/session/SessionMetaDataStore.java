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
package org.riotfamily.riot.security.session;

import java.util.List;

import org.riotfamily.riot.security.auth.RiotUser;

/**
 * Interface to load and persist {@link SessionMetaData}.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public interface SessionMetaDataStore {

	public List<SessionMetaData> listAll();
	
	public SessionMetaData sessionStarted(String userName, RiotUser user, String loginIP);
	
	public void sessionEnded(SessionMetaData data);

}

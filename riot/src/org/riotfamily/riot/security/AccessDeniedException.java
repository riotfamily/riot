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

import org.riotfamily.riot.security.auth.RiotUser;
import org.riotfamily.riot.security.policy.AuthorizationPolicy;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class AccessDeniedException extends RuntimeException {

	private RiotUser user;
	
	private String action;
	
	private Object object;

	private AuthorizationPolicy policy;

	public AccessDeniedException(RiotUser user, String action, Object object, 
			AuthorizationPolicy policy) {
		
		this.user = user;
		this.action = action;
		this.object = object;
		this.policy = policy;
	}

	public RiotUser getUser() {
		return this.user;
	}

	public String getAction() {
		return this.action;
	}

	public Object getObject() {
		return this.object;
	}

	public AuthorizationPolicy getPolicy() {
		return this.policy;
	}
	
}

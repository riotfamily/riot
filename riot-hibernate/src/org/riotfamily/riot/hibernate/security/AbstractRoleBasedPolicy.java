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

import org.riotfamily.riot.security.auth.RiotUser;
import org.riotfamily.riot.security.policy.AuthorizationPolicy;

public abstract class AbstractRoleBasedPolicy implements AuthorizationPolicy {

	private int order = Integer.MAX_VALUE - 1;

    public int getOrder() {
		return this.order;
	}

    public void setOrder(int order) {
		this.order = order;
	}

	public final int checkPermission(RiotUser riotUser, String action,
			Object object) {

		User user = (User) riotUser;
		return checkRolePermission(user.getRole(), action, object);
	}

	protected abstract int checkRolePermission(String role, String action,
				Object object);

}

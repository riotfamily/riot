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
 *   Jan-Frederic Linde [jfl at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.riot.security;

import org.riotfamily.pages.model.Page;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.security.auth.RiotUser;
import org.riotfamily.riot.security.policy.ReflectionPolicy;

public class SystemPagePolicy extends ReflectionPolicy {
	
	public SystemPagePolicy() {
		setOrder(Integer.MAX_VALUE - 3);
	}
	
	public Permission delete(RiotUser riotUser, Page page, CommandContext context) {
		if (page.getNode().isSystemNode()) {
			return Permission.DENIED;
		}
		return Permission.ABSTAIN;
	}
	
	public Permission unpublish(RiotUser riotUser, Page page, CommandContext context) {
		if (page.getNode().isSystemNode()) {
			return Permission.DENIED;
		}
		return Permission.ABSTAIN;
	}

}

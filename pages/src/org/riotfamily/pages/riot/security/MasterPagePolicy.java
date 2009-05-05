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

import static org.riotfamily.core.security.policy.AuthorizationPolicy.Permission.ABSTAIN;
import static org.riotfamily.core.security.policy.AuthorizationPolicy.Permission.DENIED;

import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.security.auth.RiotUser;
import org.riotfamily.core.security.policy.ReflectionPolicy;
import org.riotfamily.pages.model.Page;

public class MasterPagePolicy extends ReflectionPolicy {
	
	public MasterPagePolicy() {
		setOrder(Integer.MAX_VALUE - 2);
	}
	
	public Permission translatePage(RiotUser user, Page page, CommandContext context) {
		if (context.getParent() == null 
				|| page.getSite().equals(context.getParent())) {
			
			return DENIED;
		}
		return ABSTAIN;
	}
	
	public Permission getPermission(RiotUser user, String action, 
			Page page, CommandContext context) {
		
		if (context.getParent() != null 
				&& !page.getSite().equals(context.getParent())) {
			
			return DENIED;
		}
		return ABSTAIN;
	}
	
}

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
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.page.support;

import org.riotfamily.pages.page.Page;
import org.riotfamily.riot.editor.EditorDefinition;
import org.riotfamily.riot.security.policy.AuthorizationPolicy;

/**
 * AuthorizationPolicy that denies the use of the cut and delete commands
 * on system pages.
 */
public class SystemPagePolicy implements AuthorizationPolicy {

	private int order = Integer.MAX_VALUE - 1;

	public int getOrder() {
		return this.order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
	
	public int checkPermission(String subject, String action, Object object, 
			EditorDefinition editor) {
		
		if (object instanceof Page && ("cut".equals(action) 
				|| "delete".equals(action))) {
			
			Page page = (Page) object;
			if (page.isSystemPage()) {
				return ACCESS_DENIED;
			}
		}
		return ACCESS_ABSTAIN;
	}
}

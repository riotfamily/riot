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
package org.riotfamily.riot.hibernate.security;

import java.util.HashMap;

import org.riotfamily.riot.editor.EditorDefinition;
import org.riotfamily.riot.security.LoginManager;
import org.riotfamily.riot.security.policy.AuthorizationPolicy;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public abstract class AbstractRoleBasedPolicy extends HibernateDaoSupport 
		implements AuthorizationPolicy {

	private int order = Integer.MAX_VALUE - 1;
	
	private HashMap roles = new HashMap();
	
    public int getOrder() {
		return this.order;
	}
	
    public void setOrder(int order) {
		this.order = order;
	}
    
	public final int checkPermission(String subject, String action, 
			Object object, EditorDefinition editor) {
		
		if (LoginManager.ACTION_LOGIN.equals(action)) {
			invalidateRole(subject);
		}
		return checkRolePermission(getRole(subject), action, object, editor);
	}
	
	protected void invalidateRole(String userId) {
		roles.remove(userId);
	}
	
	protected String getRole(String userId) {
		String role = (String) roles.get(userId);
		if (role == null) {
			User user = (User) getHibernateTemplate().load(User.class, userId);
			role = user.getRole();
			roles.put(userId, role);
		}
		return role;
	}
	
	protected abstract int checkRolePermission(String role, String action, 
			Object object, EditorDefinition editor);
	
}

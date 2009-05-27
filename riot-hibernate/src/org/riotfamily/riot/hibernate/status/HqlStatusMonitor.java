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
package org.riotfamily.riot.hibernate.status;

import org.hibernate.SessionFactory;
import org.riotfamily.core.screen.ScreenContext;
import org.riotfamily.core.status.I18nStatusMonitor;
import org.springframework.beans.factory.annotation.Required;

public class HqlStatusMonitor extends I18nStatusMonitor {	
	
	private SessionFactory sessionFactory;
	
	private String hql;
	
	public HqlStatusMonitor(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Required
	public void setHql(String hql) {
		this.hql = hql;
	}

	protected Object[] getArgs(ScreenContext context) {	
		Object result = sessionFactory.getCurrentSession()
				.createQuery(hql)
				.uniqueResult();
		
		if (result == null) {
			return null;
		}
		else if (result instanceof Object[]) {
			return (Object[]) result;			
		}
		else {
			return new Object[] { result };
		}
	}

}

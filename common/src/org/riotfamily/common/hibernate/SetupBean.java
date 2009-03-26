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
package org.riotfamily.common.hibernate;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class SetupBean extends HibernateDaoSupport implements InitializingBean {

	private List<?> objects;

	private String condition;
		
	public void setObjects(List<?> objects) {
		this.objects = objects;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	protected void initDao() throws Exception {
		if (setupRequired()) {
			for (Object object : objects) {
				getHibernateTemplate().save(object);
			}
		}
	}
	
	protected boolean setupRequired() {
		if (condition == null) {
			return true;
		}
		Object test = getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) 
					throws HibernateException, SQLException {
				
				Query query = session.createQuery(condition);
				query.setMaxResults(1);
				return query.uniqueResult();
			};
		});
		if (test instanceof Number) {
			return ((Number) test).intValue() == 0;
		}
		if (test instanceof Boolean) {
			return ((Boolean) test).booleanValue();
		}
		return test == null;
	}
	
}

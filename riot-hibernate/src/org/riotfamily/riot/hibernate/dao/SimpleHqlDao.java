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
package org.riotfamily.riot.hibernate.dao;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.riotfamily.common.beans.PropertyUtils;
import org.riotfamily.riot.dao.ListParams;
import org.springframework.dao.DataAccessException;

public class SimpleHqlDao extends AbstractHibernateRiotDao {

	private String hql;
	
	public SimpleHqlDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public void setHql(String hql) {
		this.hql = hql.replaceAll(":parent\\.(\\w+)", ":parent_$1");
	}
	
	@Override
	protected List<?> listInternal(Object parent, ListParams params)
			throws DataAccessException {

		if (hql != null) {
			Query query = getSession().createQuery(hql);
			setQueryParameters(query, parent);
			return query.list();
		}
		else {
			return super.listInternal(parent, params);
		}
	}
	
	protected void setQueryParameters(Query query, Object parent) {
		 if (parent != null) {
			for (String param : query.getNamedParameters()) {
				Object value = parent;
				Matcher m = Pattern.compile("parent(?:_(\\w+))?").matcher(param);
				if (m.matches()) {
					String nested = m.group(1);
					if (nested != null) {
						value = PropertyUtils.getProperty(parent, nested);
					}
				}
				query.setParameter(param, value);
			}
        }
	}
}

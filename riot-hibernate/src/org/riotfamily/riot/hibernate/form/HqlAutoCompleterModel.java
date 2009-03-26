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
package org.riotfamily.riot.hibernate.form;

import java.util.Collection;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.riotfamily.core.screen.form.FormUtils;
import org.riotfamily.forms.element.suggest.AutocompleteTextField;
import org.riotfamily.forms.element.suggest.AutocompleterModel;
import org.riotfamily.riot.hibernate.support.HibernateSupport;

public class HqlAutoCompleterModel extends HibernateSupport 
		implements AutocompleterModel {

	private String hql;
	
	public HqlAutoCompleterModel(SessionFactory sessionFactory) {
		setSessionFactory(sessionFactory);
	}
	
	public void setHql(String hql) {
		this.hql = hql;
	}
	
	@SuppressWarnings("unchecked")
	public Collection<String> getSuggestions(String search,
			AutocompleteTextField element) {
		
		Query query = getSession().createQuery(hql);
		query.setParameter("search", "%" + search  + "%");
		if (hql.indexOf(":parent") != -1) {
			query.setParameter("parent", FormUtils.loadParent(element.getForm()));	
		}
		return query.list();
	}

}

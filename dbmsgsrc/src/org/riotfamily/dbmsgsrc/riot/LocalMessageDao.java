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
package org.riotfamily.dbmsgsrc.riot;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.riotfamily.dbmsgsrc.model.Message;
import org.riotfamily.dbmsgsrc.model.MessageBundleEntry;
import org.riotfamily.dbmsgsrc.support.DbMessageSource;
import org.riotfamily.pages.model.Site;
import org.riotfamily.riot.dao.ListParams;
import org.riotfamily.riot.hibernate.dao.AbstractHqlDao;

public class LocalMessageDao extends AbstractHqlDao {

	private String bundle = DbMessageSource.DEFAULT_BUNDLE;
	
	public LocalMessageDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}
	
	public Class<?> getEntityClass() {
		return Message.class;
	}
	
	public void setBundle(String bundle) {
		this.bundle = bundle;
	}

	@Override
	protected String getSelect() {
		return "coalesce(lm, dm) as this";
	}

	@Override
	protected String getFrom() {
		return MessageBundleEntry.class.getName()  
				+ " as e left join e.messages lm with lm.text is not null" 
				+ " and lm.locale = :locale "
				+ "join e.messages dm with dm.locale = :default";
	}

	@Override
	protected String getWhere() {
		return "e.bundle = :bundle";
	}
	
	@Override
	protected String getWhereClause(Object parent, ListParams params) {
		return mapAliases(super.getWhereClause(parent, params));
	}
	
	@Override
	protected String getOrderBy(ListParams params) {
		return mapAliases(super.getOrderBy(params));
	}
	
	private String mapAliases(String hql) {
		if (hql == null) {
			return null;
		}
		return hql.replace("this.entry", "e")
				.replace("e.defaultMessage", "dm")		
				.replace("this.text", "lm.text");
	}
	
	@Override
	protected void setQueryParameters(Query query, Object parent,
			ListParams params) {
		
		super.setQueryParameters(query, parent, params);
		Site site = (Site) parent;
		query.setParameter("bundle", bundle);
		query.setParameter("locale", site.getLocale());
		query.setParameter("default", MessageBundleEntry.C_LOCALE);
	}

}

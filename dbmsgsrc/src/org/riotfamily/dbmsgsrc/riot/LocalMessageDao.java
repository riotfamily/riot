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

import java.util.List;

import org.hibernate.SessionFactory;
import org.riotfamily.dbmsgsrc.model.Message;
import org.riotfamily.dbmsgsrc.model.MessageBundleEntry;
import org.riotfamily.dbmsgsrc.support.DbMessageSource;
import org.riotfamily.pages.model.Site;
import org.riotfamily.riot.dao.ListParams;
import org.riotfamily.riot.hibernate.dao.AbstractHibernateRiotDao;
import org.springframework.dao.DataAccessException;

public class LocalMessageDao extends AbstractHibernateRiotDao {

	private String bundle = DbMessageSource.DEFAULT_BUNDLE;
	
	public LocalMessageDao(SessionFactory sessionFactory) {
		super(sessionFactory);
		setEntityClass(Message.class);
	}
	
	public void setBundle(String bundle) {
		this.bundle = bundle;
	}

	@Override
	protected List<?> listInternal(Object parent, ListParams params)
			throws DataAccessException {
		
		Site site = (Site) parent;
		return getSession().createQuery(
				"select coalesce(lm, dm) from MessageBundleEntry e"
				+ " left join e.messages lm with lm.text is not null" 
				+ " and lm.locale = :locale"
				+ " left join e.messages dm with dm.locale = :default"
				+ " where e.bundle = :bundle")
				.setParameter("locale", site.getLocale())
				.setParameter("default", MessageBundleEntry.C_LOCALE)
				.setParameter("bundle", bundle)
				.list();
	}
	
}

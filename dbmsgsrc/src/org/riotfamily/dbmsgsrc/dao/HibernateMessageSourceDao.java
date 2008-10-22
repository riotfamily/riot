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
package org.riotfamily.dbmsgsrc.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.riotfamily.dbmsgsrc.model.MessageBundleEntry;
import org.riotfamily.riot.hibernate.support.HibernateHelper;
import org.springframework.transaction.annotation.Transactional;

public class HibernateMessageSourceDao implements DbMessageSourceDao {

	private HibernateHelper hibernate;
	
	public HibernateMessageSourceDao(SessionFactory sessionFactory) {
		hibernate = new HibernateHelper(sessionFactory, "messages");
	}

	public MessageBundleEntry findEntry(String bundle, String code) {
		return (MessageBundleEntry) hibernate.createCacheableCriteria(
				MessageBundleEntry.class)
				.add(Restrictions.naturalId()
					.set("bundle", bundle)
					.set("code", code))
				.uniqueResult();
	}

	@Transactional
	public void saveEntry(MessageBundleEntry entry) {
		hibernate.save(entry);
	}
	
	@Transactional
	@SuppressWarnings("unchecked")
	public void removeEmptyEntries(String bundle) {
		List<MessageBundleEntry> entries = hibernate.createCacheableCriteria(
				MessageBundleEntry.class)
				.add(Restrictions.sizeLe("messages", 1))
				.add(Restrictions.naturalId().set("bundle", bundle))
				.list();
		
		for (MessageBundleEntry entry : entries) {
			hibernate.delete(entry);
		}
	}

}

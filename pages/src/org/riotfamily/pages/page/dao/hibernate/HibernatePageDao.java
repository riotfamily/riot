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
package org.riotfamily.pages.page.dao.hibernate;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.riotfamily.pages.page.PageAlias;
import org.riotfamily.pages.page.PersistentPage;
import org.riotfamily.pages.page.dao.PageDao;
import org.riotfamily.riot.hibernate.support.HibernateSupport;

/**
 * PageDao implementation based on Hibernate.
 */
public class HibernatePageDao extends HibernateSupport implements PageDao {

	public HibernatePageDao(SessionFactory sessionFactory) {
		setSessionFactory(sessionFactory);
	}

	public List listRootPages() {
		return createQuery("from PersistentPage where parent is null " +
				"order by position").list();
	}

	public List listAliases() {
		return createCriteria(PageAlias.class).list();
	}

	public void deleteAlias(String path) {
		Query query = createQuery("delete from PageAlias where path = :path");
		query.setParameter("path", path);
		query.executeUpdate();
	}

	public void addAlias(String path, PersistentPage page) {
		PageAlias alias = new PageAlias(path, page);
		getSession().save(alias);
	}

	public void clearAliases(PersistentPage page) {
		Query query = createQuery("update PageAlias set page = null" +
				" where page = :page");
		
		query.setParameter("page", page);
		query.executeUpdate();		
	}

	public void deletePage(PersistentPage page) {
		getSession().delete(page);
	}

	public PersistentPage loadPage(Long id) {
		return (PersistentPage) getSession().get(PersistentPage.class, id);
	}

	public void savePage(PersistentPage page) {
		getSession().save(page);
	}

	public void updatePage(PersistentPage page) {
		getSession().update(page);
	}

}

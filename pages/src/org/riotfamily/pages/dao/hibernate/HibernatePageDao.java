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
package org.riotfamily.pages.dao.hibernate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.pages.Page;
import org.riotfamily.pages.PageAlias;
import org.riotfamily.pages.PageLocation;
import org.riotfamily.pages.PageNode;
import org.riotfamily.pages.dao.AbstractPageDao;
import org.springframework.util.ObjectUtils;

/**
 * PageDao implementation that uses Hibernate.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @author Jan-Frederic Linde [jfl at neteye dot de]
 * @since 6.5
 */
public class HibernatePageDao extends AbstractPageDao {

	private static final Log log = LogFactory.getLog(HibernatePageDao.class);
	
	private SessionFactory sessionFactory;
	
	public HibernatePageDao(ComponentDao componentDao, 
			SessionFactory sessionFactory) {
		
		super(componentDao);
		this.sessionFactory = sessionFactory;
	}

	public Page loadPage(Long id) {
		Session session = sessionFactory.getCurrentSession();
		return (Page) session.load(Page.class, id);
	}
	
	public Page findPage(PageLocation location) {
		Session session = sessionFactory.getCurrentSession();
		Criteria c = session.createCriteria(Page.class);
		c.add(Expression.eq("path", location.getPath()));
		if (location.getLocale() != null) {
			c.add(Expression.eq("locale", location.getLocale()));
		}
		else {
			c.add(Expression.isNull("locale"));
		}
		c.setCacheable(true);
		c.setCacheRegion("pages");
		return (Page) c.uniqueResult();
	}
	
	public PageNode getRootNode() {
		Session session = sessionFactory.getCurrentSession();
		Criteria c = session.createCriteria(PageNode.class)
				.add(Expression.isNull("parent"));
		
		PageNode root = (PageNode) c.uniqueResult();
		if (root == null) {
			root = new PageNode();
			session.save(root);
		}
		return root;
	}
	
	public void updateNode(PageNode node) {
		Session session = sessionFactory.getCurrentSession();
		session.update(node);
		session.flush();
	}
	
	protected void deleteNode(PageNode node) {
		Session session = sessionFactory.getCurrentSession();
		session.delete(node);
	}
	
	protected void updatePageWithoutChecks(Page page) {
		Session session = sessionFactory.getCurrentSession();
		session.update(page);
	}
	
	protected boolean isDirty(Page page) {
		Session session = sessionFactory.getCurrentSession();
		Page oldPage = (Page) session.load(Page.class, page.getId());
		boolean dirty = !ObjectUtils.nullSafeEquals(
				page.getPathComponent(), oldPage.getPathComponent());
		
		session.evict(oldPage);
		return dirty;
	}
		
	public PageAlias findPageAlias(PageLocation location) {
		Session session = sessionFactory.getCurrentSession();
		return (PageAlias) session.get(PageAlias.class, location);
	}
	
	protected void deleteAlias(PageLocation location) {
		Session session = sessionFactory.getCurrentSession();
		PageAlias alias = findPageAlias(location);
		if (alias != null) {
			log.info("Deleting " + alias);
			session.delete(alias);
		}
	}
	
	protected void createAlias(Page page, PageLocation location) {
		Session session = sessionFactory.getCurrentSession();
		if (page != null) {
			deleteAlias(new PageLocation(page));
		}
		PageAlias alias = new PageAlias(page, location);
		log.info("Creating " + alias);
		session.save(alias);
	}
	
	protected void clearAliases(Page page) {
		log.info("Clearing aliases for " + page);
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("update " + PageAlias.class.getName()
				+  " alias set alias.page = null where alias.page = :page");
		
		query.setParameter("page", page);
		query.executeUpdate();
		createAlias(null, new PageLocation(page));
	}
	 
}

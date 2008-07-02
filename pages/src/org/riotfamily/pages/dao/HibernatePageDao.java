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
package org.riotfamily.pages.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.PageAlias;
import org.riotfamily.pages.model.PageNode;
import org.riotfamily.pages.model.Site;
import org.riotfamily.riot.hibernate.support.HibernateHelper;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.util.Assert;

/**
 * PageDao implementation that uses Hibernate.
 *
 * @author Felix Gnass [fgnass at neteye dot de]
 * @author Jan-Frederic Linde [jfl at neteye dot de]
 * @since 6.5
 */
public class HibernatePageDao extends AbstractPageDao {

	private static final Log log = LogFactory.getLog(HibernatePageDao.class);

	private HibernateHelper hibernate;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.hibernate = new HibernateHelper(sessionFactory, "pages");
	}

	protected void initDao() {
		Assert.notNull(hibernate, "A SessionFactory must be  set.");
	}

	protected Object loadObject(Class<?> clazz, Serializable id) {
		return hibernate.get(clazz, id);
	}

	protected void saveObject(Object object) {
		hibernate.persist(object);
	}

	protected void deleteObject(Object object) {
		hibernate.delete(object);
	}
	
	public List<Site> listSites() {
		Criteria c = hibernate.createCacheableCriteria(Site.class);
		c.addOrder(Order.asc("position"));
		return hibernate.list(c);
	}

	public Site getDefaultSite() {
		Criteria c = hibernate.createCacheableCriteria(Site.class);
		c.setMaxResults(1);
		return (Site) hibernate.uniqueResult(c);
	}
	
	public PageNode getRootNode() {
		Criteria c = hibernate.createCacheableCriteria(PageNode.class);
		c.add(Restrictions.isNull("parent"));
		PageNode rootNode = (PageNode) hibernate.uniqueResult(c);
		if (rootNode == null) {
			rootNode = new PageNode();
			saveNode(rootNode);
		}
		return rootNode;
	}
	
	public Site findSite(Locale locale) {
		Criteria c = hibernate.createCacheableCriteria(Site.class);
		c.add(Restrictions.eq("locale", locale));
		List sites = hibernate.list(c);
		if (sites.size() == 0) {
			return null;
		}
		else if (sites.size() == 1) {
			return (Site) sites.get(0);
		}	
		else {			
			throw new IncorrectResultSizeDataAccessException(
						"More than one site found for locale " + locale, 1);
		}				
	}
	
	public Site mergeSite(Site site) {
		return hibernate.merge(site);
	}

	public Page findPage(Site site, String path) {
		Criteria c = hibernate.createCacheableCriteria(Page.class);
		c.add(Restrictions.eq("site", site));
		c.add(Restrictions.eq("path", path));
		return (Page) hibernate.uniqueResult(c);
	}

	public Page mergePage(Page page) {
		return hibernate.merge(page);
	}
	
	public PageNode findNodeOfType(String pageType) {
		Criteria c = hibernate.createCacheableCriteria(PageNode.class);
		c.createCriteria("node").add(Restrictions.eq("pageType", pageType));
		return (PageNode) hibernate.uniqueResult(c);
	}

	public Page findPageOfType(String pageType, Site site) {
		Criteria c = hibernate.createCacheableCriteria(Page.class);
		c.add(Restrictions.eq("site", site));
		c.createCriteria("node").add(Restrictions.eq("pageType", pageType));
		return (Page) hibernate.uniqueResult(c);
	}

	public List findPagesOfType(String pageType, Site site) {
		Criteria c = hibernate.createCacheableCriteria(Page.class);
		c.add(Restrictions.eq("site", site));
		c.createCriteria("node").add(Restrictions.eq("pageType", pageType));
		return hibernate.list(c);
	}
	
	public List getWildcardPaths(Site site) {
		Criteria c = hibernate.createCacheableCriteria(Page.class);
		c.setProjection(Projections.property("path"));
		c.add(Restrictions.eq("wildcardInPath", Boolean.TRUE));
		c.add(Restrictions.eq("site", site));
		return hibernate.list(c);
	}

	public PageAlias findPageAlias(Site site, String path) {
		Criteria c = hibernate.createCacheableCriteria(PageAlias.class);
		c.add(Restrictions.eq("site", site));
		c.add(Restrictions.eq("path", path));
		return (PageAlias) hibernate.uniqueResult(c);
	}

	protected void deleteAliases(Site site) {
		log.info("Deleting aliases for " + site);
		Query query = hibernate.createQuery("delete " 
				+ PageAlias.class.getName()
				+  " alias where alias.site = :site");

		hibernate.setParameter(query, "site", site);
		hibernate.executeUpdate(query);
	}
	
	protected void clearAliases(Page page) {
		log.info("Clearing aliases for " + page);
		Query query = hibernate.createQuery("update " + PageAlias.class.getName()
				+  " alias set alias.page = null where alias.page = :page");

		hibernate.setParameter(query, "page", page);
		hibernate.executeUpdate(query);
		createGoneAlias(page.getSite(), page.getPath());
	}

}

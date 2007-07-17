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

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.riotfamily.pages.Page;
import org.riotfamily.pages.PageAlias;
import org.riotfamily.pages.PageLocation;
import org.riotfamily.pages.PageNode;
import org.riotfamily.pages.Site;
import org.riotfamily.pages.dao.AbstractPageDao;
import org.riotfamily.riot.hibernate.support.HibernateHelper;
import org.riotfamily.riot.hibernate.support.HibernateUtils;
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

	protected Object loadObject(Class clazz, Serializable id) {
		return hibernate.load(clazz, id);
	}

	protected void saveObject(Object object) {
		hibernate.save(object);
	}

	protected void updateObject(Object object) {
		hibernate.update(object);
		hibernate.flush(); //REVIST
	}

	protected void reattachObject(Object object) {
		hibernate.lock(object, LockMode.NONE);
	}

	protected void deleteObject(Object object) {
		hibernate.delete(object);
	}

	public List listSites() {
		Criteria c = hibernate.createCacheableCriteria(Site.class);
		return hibernate.list(c);
	}

	public Site getSite(String name) {
		if (name == null) {
			return getDefaultSite();
		}
		Criteria c = hibernate.createCacheableCriteria(Site.class);
		c.add(Restrictions.eq("name", name));
		return (Site) hibernate.uniqueResult(c);
	}

	public PageNode findRootNode(Site site) {
		Criteria c = hibernate.createCacheableCriteria(PageNode.class);
		c.add(Restrictions.isNull("parent"));
		c.add(Restrictions.eq("site", site));
		return (PageNode) hibernate.uniqueResult(c);
	}

	public Page findPage(PageLocation location) {
		String siteName = location.getSiteName();
		Site site = siteName != null ? getSite(siteName) : getDefaultSite();
		Criteria c = hibernate.createCacheableCriteria(Page.class);
		c.createCriteria("node").add(Restrictions.eq("site", site));
		c.add(Restrictions.eq("path", location.getPath()));
		HibernateUtils.addEqOrNull(c, "locale", location.getLocale());
		return (Page) hibernate.uniqueResult(c);
	}

	public PageNode findNodeForHandler(String handlerName) {
		Criteria c = hibernate.createCacheableCriteria(PageNode.class)
				.add(Restrictions.eq("handlerName", handlerName));

		return (PageNode) hibernate.uniqueResult(c);
	}

	public Page findPageForHandler(String handlerName, Locale locale) {
		return (Page) hibernate.uniqueResult(
				createPageQuery(handlerName, locale));
	}

	public List findPagesForHandler(String handlerName, Locale locale) {
		return hibernate.list(createPageQuery(handlerName, locale));
	}

	private Query createPageQuery(String handlerName, Locale locale) {
		StringBuffer hql = new StringBuffer("from ")
			.append(Page.class.getName())
			.append(" where node.handlerName = :handlerName and locale ")
			.append(locale != null ? "= :locale" : "is null");

		Query query = hibernate.createCacheableQuery(hql.toString());
		hibernate.setParameter(query, "handlerName", handlerName);
		hibernate.setParameter(query, "locale", locale);
		return query;
	}

	public void refreshPage(Page page) {
		hibernate.refresh(page);
	}

	public PageAlias findPageAlias(PageLocation location) {
		if (location.getSiteName() == null) {
			location.setSiteName(getDefaultSite().getName());
		}
		return (PageAlias) hibernate.get(PageAlias.class, location);
	}


	protected void clearAliases(Page page) {
		log.info("Clearing aliases for " + page);
		Query query = hibernate.createQuery("update " + PageAlias.class.getName()
				+  " alias set alias.page = null where alias.page = :page");

		hibernate.setParameter(query, "page", page);
		hibernate.executeUpdate(query);
		createAlias(null, new PageLocation(page));
	}


}

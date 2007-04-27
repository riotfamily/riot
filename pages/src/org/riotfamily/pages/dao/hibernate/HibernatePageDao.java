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

import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.pages.Page;
import org.riotfamily.pages.PageAlias;
import org.riotfamily.pages.PageLocation;
import org.riotfamily.pages.PageNode;
import org.riotfamily.pages.dao.AbstractPageDao;
import org.riotfamily.riot.hibernate.support.HibernateHelper;
import org.riotfamily.riot.hibernate.support.HibernateUtils;

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

	public HibernatePageDao(ComponentDao componentDao,
			SessionFactory sessionFactory) {

		super(componentDao);
		this.hibernate = new HibernateHelper(sessionFactory, "pages");
	}

	public Page loadPage(Long id) {
		return (Page) hibernate.load(Page.class, id);
	}

	public Page findPage(PageLocation location) {
		Criteria c = hibernate.createCacheableCriteria(Page.class);
		c.add(Expression.eq("path", location.getPath()));
		HibernateUtils.addEqOrNull(c, "locale", location.getLocale());
		return (Page) hibernate.uniqueResult(c);
	}

	public PageNode getRootNode() {
		Criteria c = hibernate.createCacheableCriteria(PageNode.class)
				.add(Expression.isNull("parent"));

		PageNode root = (PageNode) c.uniqueResult();
		if (root == null) {
			root = new PageNode();
			hibernate.save(root);
		}
		return root;
	}

	public PageNode findNodeForHandler(String handlerName) {
		Criteria c = hibernate.createCacheableCriteria(PageNode.class)
				.add(Expression.eq("handlerName", handlerName));

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

	public void updateNode(PageNode node) {
		hibernate.update(node);
		hibernate.flush();
	}

	protected void deleteNode(PageNode node) {
		hibernate.delete(node);
	}

	public void reattachPage(Page page) {
		updatePageWithoutChecks(page);
	}

	protected void updatePageWithoutChecks(Page page) {
		hibernate.update(page);
	}

	public PageAlias findPageAlias(PageLocation location) {
		return (PageAlias) hibernate.get(PageAlias.class, location);
	}

	protected void deleteAlias(PageLocation location) {
		PageAlias alias = findPageAlias(location);
		if (alias != null) {
			log.info("Deleting " + alias);
			hibernate.delete(alias);
		}
	}

	protected void createAlias(Page page, PageLocation location) {
		if (page != null) {
			deleteAlias(new PageLocation(page));
		}
		PageAlias alias = new PageAlias(page, location);
		log.info("Creating " + alias);
		hibernate.save(alias);
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

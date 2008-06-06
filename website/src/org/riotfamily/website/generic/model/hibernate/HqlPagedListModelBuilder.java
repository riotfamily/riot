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
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.website.generic.model.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Query;
import org.riotfamily.riot.hibernate.support.HibernateUtils;
import org.riotfamily.website.generic.view.Pager;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.web.bind.ServletRequestUtils;

/**
 * HqlModelBuilder that supports large lists and provides pagination. In
 * addition to the HQL query that returns the list items, a second query must be
 * specified, that returns the total number of items.
 */
public class HqlPagedListModelBuilder extends HqlListModelBuilder {

	private String pagerModelKey = "pager";

	private String countHql;

	private String pageParam = "page";

	private String pageSizeParam;

	private int pageSize = 10;

	private int padding = 3;

	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		if (countHql == null) {
			if (getHql().startsWith("from")) {
				countHql = "select count(*) " + getHql();
				int i = countHql.indexOf(" order by ");
				if (i != -1) {
					countHql = countHql.substring(0, i);
				}
			}
			else {
				throw new BeanCreationException("The property 'countHql' must "
						+ "be set if hql doesn't starts with 'from'");
			}
		}
	}

	/**
	 * Sets the HQL query that returns the total number of items.
	 */
	public void setCountHql(String countHql) {
		this.countHql = countHql;
	}

	/**
	 * Sets the name of the HTTP parameter that contains the current page
	 * number. Please note that page numbers start at <code>1</code>.
	 */
	public void setPageParam(String pageParam) {
		this.pageParam = pageParam;
	}

	/**
	 * Sets the key under which the {@link Pager pager bean} will be placed in
	 * the model. Defaults to <code>pager</code>
	 */
	public void setPagerModelKey(String pagerModelKey) {
		this.pagerModelKey = pagerModelKey;
	}

	/**
	 * Sets the page size, i.e. the number of items displayed on each page.
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	/**
	 * Does the same as {@link #setPageSize(int)}.
	 */
	public void setMaxResults(Integer maxResults) {
		if (maxResults != null) {
			setPageSize(maxResults.intValue());
		}
	}

	/**
	 * Sets the name of the HTTP parameter that determines the page size.
	 * Default is <code>null</code>, which disables the variable page size
	 * feature.
	 */
	public void setPageSizeParam(String pageSizeParam) {
		this.pageSizeParam = pageSizeParam;
	}

	/**
	 * Sets the number of pages that will be displayed before and after the
	 * current page.
	 *
	 * @see Pager
	 */
	public void setPadding(int padding) {
		this.padding = padding;
	}

	/**
	 * Returns the current page number for the given request.
	 */
	protected int getPage(HttpServletRequest request) {
		return ServletRequestUtils.getIntParameter(request, pageParam, 1);
	}

	/**
	 * Returns the page-size for the given request.
	 */
	protected int getPageSize(HttpServletRequest request) {
		if (pageSizeParam == null) {
			return pageSize;
		}
		return ServletRequestUtils.getIntParameter(request, pageSizeParam,
				pageSize);
	}

	public Map buildModel(final HttpServletRequest request) {
		HibernateUtils.enableLiveModeFilterIfNecessary(getSession());
		final int page = getPage(request);
		final int pageSize = getPageSize(request);

		Query query = createQuery(getHql());
		setParameters(query, request);
		query.setFirstResult((page - 1) * pageSize);
		query.setMaxResults(pageSize);
		List items = query.list();

		Query countQuery = createQuery(countHql);
		setParameters(countQuery, request);
		Number count = (Number)countQuery.uniqueResult();

		Pager pager = new Pager(page, pageSize, count.longValue());
		pager.initialize(request, padding, pageParam);

		tagResult(query, items, request);
		
		HashMap<String, Object> model = new HashMap<String, Object>();
		model.put(getModelKey(query), items);
		model.put(pagerModelKey, pager);
		return model;
	}

	public void appendCacheKey(StringBuffer key, HttpServletRequest request) {
		super.appendCacheKey(key, request);
		key.append("?page=").append(getPage(request));
		key.append("&pageSize=").append(getPageSize(request));
	}
}

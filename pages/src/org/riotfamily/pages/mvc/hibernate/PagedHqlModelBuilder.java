package org.riotfamily.pages.mvc.hibernate;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.riotfamily.common.collection.FlatMap;
import org.riotfamily.common.web.view.Pager;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.web.bind.ServletRequestUtils;

/**
 * HqlModelBuilder that supports large lists and provides pagination. In
 * addition to the HQL query that returns the list items, a second query must be
 * specified, that returns the total number of items.
 */
public class PagedHqlModelBuilder extends HqlModelBuilder {

	private String pagerModelKey = "pager";

	private String countHql;

	private String pageParam = "page";

	private String pageSizeParam;

	private int pageSize = 10;

	private int padding = 3;

	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		if (this.countHql == null) {
			if (getHql().startsWith("from")) {
				this.countHql = new StringBuffer("select count(*) ").append(
						getHql()).toString();
			}
			else {
				throw new BeanCreationException(
						"CountHql must be set if hql doesn't starts with >from<");
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
	 * Returns the curent page number for the given request.
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
		final int page = getPage(request);
		final int pageSize = getPageSize(request);

		List items = getHibernateTemplate().executeFind(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {

						Query query = session.createQuery(getHql());
						setParameters(query, request);
						query.setFirstResult((page - 1) * pageSize);
						query.setMaxResults(pageSize);
						return query.list();
					}
				});

		Number count = (Number) getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {

						Query query = session.createQuery(countHql);
						setParameters(query, request);
						return query.uniqueResult();
					};
				});

		Pager pager = new Pager(page, pageSize, count.longValue());
		pager.initialize(request, padding, pageParam);

		tag(items, request);

		FlatMap model = new FlatMap();
		model.put(modelKey, items);
		model.put(pagerModelKey, pager);
		return model;
	}

	public void appendCacheKey(StringBuffer key, HttpServletRequest request) {
		super.appendCacheKey(key, request);
		key.append("?page=").append(getPage(request));
		key.append("&pageSize=").append(getPageSize(request));
	}
}

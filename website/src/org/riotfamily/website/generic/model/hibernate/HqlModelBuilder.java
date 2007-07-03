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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.EntityMode;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.riotfamily.cachius.TaggingContext;
import org.riotfamily.cachius.spring.CacheableController;
import org.riotfamily.common.collection.FlatMap;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.riot.hibernate.support.HibernateSupport;
import org.riotfamily.website.cache.CacheInvalidationAdvice;
import org.riotfamily.website.generic.model.CacheableModelBuilder;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;


/**
 * ModelBuilder that builds a model by executing a hibernate query.
 * Named parameters may be added to the HQL query which will be resolved using
 * a {@link ParameterResolver}.
 */
public class HqlModelBuilder extends HibernateSupport
		implements CacheableModelBuilder,
		ApplicationContextAware, InitializingBean {

	public final static String CACHE_ETERNAL_STRING = "eternal";

	private Log log = LogFactory.getLog(HqlModelBuilder.class);

	private static Pattern PARAM_PATTERN = Pattern.compile("\\:([._\\w]+)");

	private ApplicationContext applicationContext;

	protected String modelKey;

	private String hql;

	private boolean listMode;

	private ArrayList params = new ArrayList();

	private List parameterResolvers;

	private boolean tagCacheItems = true;

	private long timeToLive = CacheableController.CACHE_ETERNALLY;

	public HqlModelBuilder() {
	}

	public HqlModelBuilder(SessionFactory sessionFactory) {
		setSessionFactory(sessionFactory);
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * Sets the key under which the query result will be put into the model.
	 */
	public void setModelKey(String modelKey) {
		this.modelKey = modelKey;
	}

	/**
	 * Sets the HQL query to be executed.
	 */
	public void setHql(String hql) {
		this.hql = hql;
	}

	protected String getHql() {
		return this.hql;
	}

	/**
	 * Sets a list of {@link ParameterResolver ParameterResolver}s which
	 * are used to resolve named parameters found in the HQL query.
	 */
	public void setParameterResolvers(List parameterResolvers) {
		this.parameterResolvers = parameterResolvers;
	}

	/**
	 * Sets whether a list of objects or a single object should be returned
	 * by the query. Default is <code>false</code>.
	 */
	public void setListMode(boolean listMode) {
		this.listMode = listMode;
	}

	/**
	 * Sets whether the current cache item should be tagged.
	 * Default is <code>true</code>.
	 */
	public void setTagCacheItems(boolean tagCacheItems) {
		this.tagCacheItems = tagCacheItems;
	}

	/**
	 * Sets
	 */
	public void setTtlPeriod(String timeToLive) {
		if (!StringUtils.hasText(timeToLive)) {
			return;
		}
		timeToLive = StringUtils.trimTrailingWhitespace(timeToLive);
		if (timeToLive.equalsIgnoreCase(CACHE_ETERNAL_STRING)) {
			this.timeToLive = CacheableController.CACHE_ETERNALLY;
		}
		else {
			this.timeToLive = FormatUtils.parseMillis(timeToLive);
		}
	}


	/**
	 * @deprecated Not needed anymore.
	 */
	public void setItemClass(String clazz) {
	}


	public void afterPropertiesSet() throws Exception {
		if (getSessionFactory() == null) {
			setSessionFactory((SessionFactory)
					BeanFactoryUtils.beanOfTypeIncludingAncestors(
					applicationContext, SessionFactory.class));
		}

		if (parameterResolvers == null) {
			parameterResolvers = Collections.EMPTY_LIST;
		}

		Matcher matcher = PARAM_PATTERN.matcher(hql);
		while (matcher.find()) {
			String param = matcher.group(1);
			params.add(new Param(param, getParameterResolver(param)));
		}
	}

	/**
	 * Returns a ParameterResolver for the parameter with the given name.
	 * If no matching resolver is found a new {@link DefaultParameterResolver}
	 * is created.
	 */
	protected ParameterResolver getParameterResolver(String param) {
		Iterator it = parameterResolvers.iterator();
		while (it.hasNext()) {
			ParameterResolver resolver = (ParameterResolver) it.next();
			if (resolver.accept(param)) {
				return resolver;
			}
		}

		DefaultParameterResolver resolver = new DefaultParameterResolver();
		resolver.setParam(param);
		return resolver;
	}

	/**
	 * Returns <code>0</code>, if <code>cacheEternally</code> is set to
	 * <code>false</code>, or <code>CacheableModelBuilder.CACHE_ETERNALLY</code>
	 * otherwise. Subclasses may override this method to support modification
	 * based caching.
	 * @see org.riotfamily.website.generic.model.CacheableModelBuilder#getTimeToLive()
	 */
	public long getTimeToLive() {
		return this.timeToLive;
	}

	/**
	 * Returns <code>System.currentTimeMillis()</code>.
	 * Subclasses may override this method to support modification based caching.
	 * Since a database lookup might be nearly as expensive as building the
	 * actual model you might want to consider using item-tagging in conjunction
	 * with a {@link CacheInvalidationAdvice} instead.
	 */
	public long getLastModified(HttpServletRequest request) {
		return System.currentTimeMillis();
	}

	public Map buildModel(final HttpServletRequest request) {
		Query query = createQuery(hql);
		log.debug("Query: " + query.getQueryString());
		setParameters(query, request);

		Object result;
		if (listMode) {
			result = query.list();
			tagList(request, query);
		}
		else {
			query.setMaxResults(1);
			result = query.uniqueResult();
			tagItem(request, query, result);
		}

		FlatMap model = new FlatMap();
		model.put(modelKey, result);
		return model;
	}

	protected void tagList(HttpServletRequest request, Query query) {
		if (!tagCacheItems) {
			return;
		}
		Class clazz = query.getReturnTypes()[0].getReturnedClass();
		TaggingContext.tag(request, clazz.getName());
	}

	protected void tagItem(HttpServletRequest request, Query query, Object item) {
		if (!tagCacheItems) {
			return;
		}
		if (item != null) {
			Class clazz = Hibernate.getClass(item);
			Serializable id = getSessionFactory()
					.getClassMetadata(clazz).getIdentifier(
					item, EntityMode.POJO);

			TaggingContext.tag(request, clazz.getName() + "#" + id);
		}
		else {
			Class clazz = query.getReturnTypes()[0].getReturnedClass();
			TaggingContext.tag(request, clazz.getName());
		}
	}

	protected void setParameters(Query query, HttpServletRequest request) {
		Iterator it = params.iterator();
		while (it.hasNext()) {
			Param param = (Param) it.next();
			Object value = param.getValue(request);
			log.debug("Query parameter '" + param.name + "' = " + value);
			query.setParameter(param.name, value);
		}
	}

	public void appendCacheKey(StringBuffer key, HttpServletRequest request) {
		Iterator it = params.iterator();
		while (it.hasNext()) {
			Param param = (Param) it.next();
			param.appendToCacheKey(request, key);
		}
	}

	private class Param {

		private String name;

		private ParameterResolver resolver;

		Param (String name, ParameterResolver resolver) {
			this.name = name;
			this.resolver = resolver;
		}

		void appendToCacheKey(HttpServletRequest request, StringBuffer key) {
			if (resolver.includeInCacheKey()) {
				key.append(name).append('=');
				key.append(getValue(request)).append(';');
			}
		}

		Object getValue(HttpServletRequest request) {
			return resolver.getValue(request);
		}
	}

}
package org.riotfamily.pages.mvc.hibernate;

import java.io.Serializable;
import java.sql.SQLException;
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
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.riotfamily.cachius.spring.TaggingContext;
import org.riotfamily.common.collection.FlatMap;
import org.riotfamily.pages.mvc.cache.CacheableModelBuilder;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;


/**
 * ModelBuilder that builds a model by executing a hibernate query.
 * Named parameters may be added to the HQL query which will be resolved using
 * a {@link ParameterResolver}.
 */
public class HqlModelBuilder implements CacheableModelBuilder, 
		ApplicationContextAware, InitializingBean {

	private Log log = LogFactory.getLog(HqlModelBuilder.class);
	
	private static Pattern PARAM_PATTERN = Pattern.compile("\\:([._\\w]+)");

	private HibernateTemplate hibernateTemplate;
	
	private ApplicationContext applicationContext;
	
	protected String modelKey;
	
	private String hql;

	private boolean listMode;
	
	private ArrayList params = new ArrayList();

	private List parameterResolvers;
	
	private String itemClass;
	
	private boolean tagCacheItems = true;
	
	private boolean cacheEternally = true;
	
	public HqlModelBuilder() {
	}
	
	public HqlModelBuilder(SessionFactory sessionFactory) {
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.hibernateTemplate = new HibernateTemplate(sessionFactory);
	}
	
	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}
	
	protected HibernateTemplate getHibernateTemplate() {
		return this.hibernateTemplate;
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
	
	public void setCacheEternally(boolean cacheEternaly) {
		this.cacheEternally = cacheEternaly;
	}

	/**
	 * Sets which classname should be used for taging the cache item
	 * when the result is empty.
	 */
	public void setItemClass(String clazz) {
		this.itemClass = clazz;
	}

	
	public void afterPropertiesSet() throws Exception {
		if (hibernateTemplate == null) {
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
		
		if (itemClass == null && tagCacheItems == true) {
			throw new BeanInitializationException("ItemClass not set and " +
					"tagCacheItems set to true. This could lead to " +
					"unexpected caching results");
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
	 * <code>true</code>, or <code>-1</code> otherwise. Subclasses may override 
	 * this method to support modification based caching. Since a database 
	 * lookup might be nearly as expensive as building the actual model you 
	 * might want to consider using item-tagging in conjunction with an 
	 * {@link org.riotfamily.pages.mvc.cache.CacheItemInvalidator}
	 * instead.
	 */
	public long getLastModified(HttpServletRequest request) {
		return cacheEternally ? 0 : -1;
	}
	
	public Map buildModel(final HttpServletRequest request) {
		Object result = hibernateTemplate.execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {

				Query query = session.createQuery(hql);
				log.debug("Query: " + query.getQueryString());
				setParameters(query, request);
				if (listMode) {
					return query.list();
				}
				else {
					query.setMaxResults(1);
					return query.uniqueResult();	
				}
			}
		});
		
		tag(result, request);
		
		FlatMap model = new FlatMap();
		model.put(modelKey, result);
		return model;
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
	
	/**
	 * Tags the current cache item (if present). If the ModelBuilder operates
	 * in listMode the configured itemClass is used.
	 * Otherwise the fully qualified classname of the given object plus the
	 * entity's primary key is used (separated by a hash character).
	 * 
	 * @see TaggingContext#tag(HttpServletRequest, String)
	 */
	protected void tag(Object obj, HttpServletRequest request) {
		if (!tagCacheItems) {
			return;
		}
		
		if (listMode) {
			if (itemClass != null) {
				TaggingContext.tag(request, this.itemClass);
			}
			else {
				log.warn("Item class not set - could not tag cacheItems");
			}
		}
		else {
			if (itemClass != null) {
				TaggingContext.tag(request, this.itemClass);
			}
			if (obj != null) {
				Class clazz = Hibernate.getClass(obj);
				if (itemClass == null) {
					TaggingContext.tag(request, clazz.getName());
				}				
				Serializable id = getHibernateTemplate().getSessionFactory()
					.getClassMetadata(clazz).getIdentifier(obj, EntityMode.POJO);
				TaggingContext.tag(request, clazz.getName() + "#" + id);
			}
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
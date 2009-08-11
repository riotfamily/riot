package org.riotfamily.riot.hibernate.cachius;

import org.hibernate.SessionFactory;
import org.riotfamily.common.freemarker.ObjectWrapperPlugin;
import org.riotfamily.common.freemarker.PluginObjectWrapper;
import org.riotfamily.riot.hibernate.support.HibernateUtils;
import org.riotfamily.website.cache.CacheTagUtils;
import org.riotfamily.website.cache.TagCacheItems;
import org.riotfamily.website.cache.TaggingStringModel;
import org.springframework.core.Ordered;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * ObjectWrapperPlugin that tags cache items whenever a class with the
 * {@link TagCacheItems} annotation is accessed by a FreeMarker template.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class TaggingObjectWrapperPlugin implements ObjectWrapperPlugin, Ordered {

	private int order = Ordered.HIGHEST_PRECEDENCE;

	private SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public int getOrder() {
		return order;
	}

	/**
	 * Sets the order. Default is {@link Ordered#HIGHEST_PRECEDENCE}.
	 */
	public void setOrder(int order) {
		this.order = order;
	}

	public boolean supports(Object obj) {
		return obj.getClass().isAnnotationPresent(TagCacheItems.class);
	}

	public TemplateModel wrapSupportedObject(Object obj,
			PluginObjectWrapper wrapper) throws TemplateModelException {

		TaggingStringModel model = new TaggingStringModel(obj, wrapper);
		model.addTag(CacheTagUtils.getTag(obj.getClass(), HibernateUtils.getIdAsString(sessionFactory, obj)));
		return model;
	}
	
}

package org.riotfamily.cachius.spring;

import java.io.File;

import javax.servlet.ServletContext;

import org.riotfamily.cachius.Cache;
import org.riotfamily.cachius.CacheFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.util.WebUtils;

/**
 * Factory that creates a new {@link Cache Cache} instance in the
 * specified directory.
 */
public class CacheFactoryBean extends CacheFactory 
		implements FactoryBean, ServletContextAware, 
		BeanNameAware, InitializingBean, DisposableBean {

	private String beanName;
	
	private String cacheDirName;

	private ServletContext servletContext;

	private Cache cache;
	
	public void setBeanName(String name) {
		this.beanName = name;
	}
	
	public void setCacheDirName(String cacheDirName) {
		this.cacheDirName = cacheDirName;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public void afterPropertiesSet() throws Exception {
		if (getCacheDir() == null) {
			if (cacheDirName == null) {
				cacheDirName = beanName;
			}
			setCacheDir(new File(WebUtils.getTempDir(servletContext), 
					cacheDirName));
		}
		
	}
	
	public Class<?> getObjectType() {
		return Cache.class;
	}
	
	public boolean isSingleton() {
		return true;
	}
	
	public Object getObject() throws Exception {
		if (cache == null) {
			cache = createInstance();
		}
		return cache;
	}
	
	public void destroy() throws Exception {
		if (cache != null) {
			cache.shutdown();
			if (isRestore()) {
				persist(cache);	
			}
		}
	}

}

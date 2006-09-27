package org.riotfamily.cachius.spring;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;

import org.riotfamily.cachius.Cache;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.core.io.Resource;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.util.WebUtils;

/**
 * Factory bean that creates a new {@link Cache Cache} instance in the 
 * specified directroy.
 */
public class CacheFactoryBean extends AbstractFactoryBean 
		implements ServletContextAware {

	public static final int DEFAULT_CAPACITY = 10000;
	
	public static final String DEFAULT_CACHE_DIR_NAME = "cache";
	
	private int capacity = DEFAULT_CAPACITY;
	
	private File cacheDir;
	
	private String cacheDirName = DEFAULT_CACHE_DIR_NAME;
	
	private ServletContext servletContext;
	
	
	public void setCacheDirName(String cacheDirName) {
		this.cacheDirName = cacheDirName;
	}

	/**
	 * @throws IOException if the resource cannot be resolved as absolute 
	 * file path, i.e. if the resource is not available in a file system.
	 */
	public void setCacheDir(Resource cacheDir) throws IOException {
		this.cacheDir = cacheDir.getFile();
	}

	/**
	 * Sets the capacity of the Cache. If not set, the capacity will default
	 * to <code>DEFAULT_CAPACITY</code> (10000). 
	 */
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	/**
	 * Returns <code>Cache.class</code>.
	 */
	public Class getObjectType() {
		return Cache.class;
	}
	
	protected Object createInstance() throws Exception {
		if (cacheDir == null) {
			File tempDir = WebUtils.getTempDir(servletContext);
			cacheDir = new File(tempDir, cacheDirName);
		}
		return Cache.newInstance(capacity, cacheDir);
	}

}

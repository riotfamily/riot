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
package org.riotfamily.cachius.spring;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;

import org.riotfamily.cachius.Cache;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.core.io.Resource;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.util.WebUtils;

/**
 * Factory that creates a new {@link Cache Cache} instance in the
 * specified directory.
 */
public class CacheFactory implements ServletContextAware, BeanNameAware {

	public static final int DEFAULT_CAPACITY = 10000;

	public static final String DEFAULT_CACHE_DIR_NAME = "cache";

	private int capacity = DEFAULT_CAPACITY;

	private File cacheDir;

	private String beanName;
	
	private String cacheDirName;

	private ServletContext servletContext;

	private boolean restore = true;
	
	private boolean enabled = true;


	public void setBeanName(String name) {
		this.beanName = name;
	}
	
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

	/**
	 * Sets whether the factory should try to restore a previously persisted
	 * version of the cache. Default is <code>true</code>.
	 */
	public void setRestore(boolean restore) {
		this.restore = restore;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public Object createInstance() throws Exception {
		if (cacheDir == null) {
			File tempDir = WebUtils.getTempDir(servletContext);
			if (cacheDirName == null) {
				cacheDirName = beanName;
				if (cacheDirName == null) {
					cacheDirName = DEFAULT_CACHE_DIR_NAME;
				}
			}
			cacheDir = new File(tempDir, cacheDirName);
		}
		return Cache.newInstance(capacity, cacheDir, restore, enabled);
	}

}

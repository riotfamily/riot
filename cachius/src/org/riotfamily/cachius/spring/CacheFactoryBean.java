/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

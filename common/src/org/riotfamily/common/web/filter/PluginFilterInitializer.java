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
package org.riotfamily.common.web.filter;

import javax.servlet.ServletContext;

import org.riotfamily.common.util.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import org.springframework.web.context.ServletContextAware;

public class PluginFilterInitializer implements ServletContextAware, 
		ApplicationContextAware, InitializingBean, DisposableBean {

	private Logger log = LoggerFactory.getLogger(PluginFilterInitializer.class);
	
	private ServletContext servletContext;
	
	private String filterName;
	
	private boolean ignoreFilterNotPresent;

	private ApplicationContext applicationContext;
	
	private PluginFilter filter;
	
	/**
	 * Sets the name of the filter where the plugins should be registered.
	 */
	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}

	/**
	 * Sets whether errors caused by a missing filter should be ignored.
	 */
	public void setIgnoreFilterNotPresent(boolean ignoreFilterNotPresent) {
		this.ignoreFilterNotPresent = ignoreFilterNotPresent;
	}
	
	public final void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	/**
	 * Retrieves the {@link PluginFilter} with the specified name from the
	 * ServletContext and registers the plugin.
	 */
	public final void afterPropertiesSet() throws Exception {
		Assert.notNull(filterName, "A filterName must be set.");
		filter = PluginFilter.getInstance(servletContext, filterName);
		if (filter == null) {
			if (ignoreFilterNotPresent) {
				log.warn("Failed to register FilterPlugin because no filter "
						+ "named " + filterName + " is defined in web.xml");
				
				return;
			}
			else {
				throw new FatalBeanException(
						"No such filter defined in web.xml: " + filterName);
			}
		}
		;
		filter.setPlugins(SpringUtils.orderedBeans(applicationContext, FilterPlugin.class));
	}
	
	public final void destroy() throws Exception {
		filter.setPlugins(null);
	}
}

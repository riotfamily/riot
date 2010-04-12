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

import java.util.List;

import javax.servlet.ServletContext;

import org.riotfamily.common.util.SpringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import org.springframework.web.context.ServletContextAware;

public class PluginFilterInitializer implements ServletContextAware, 
		ApplicationContextAware, InitializingBean, DisposableBean {

	private ServletContext servletContext;
	
	private String filterName;
	
	private ApplicationContext applicationContext;
	
	/**
	 * Sets the name of the filter where the plugins should be registered.
	 */
	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}

	/**
	 * Sets whether errors caused by a missing filter should be ignored.
	 */
	@Deprecated
	public void setIgnoreFilterNotPresent(boolean ignoreFilterNotPresent) {
	}
	
	public final void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	/**
	 * Retrieves the {@link PluginFilter} with the specified name from the
	 * ServletContext and registers the plugins. If no filter is found 
	 * (probably because it has not yet been initialized), the plugins are
	 * stored in the ServletContext. 
	 */
	public final void afterPropertiesSet() throws Exception {
		Assert.notNull(filterName, "A filterName must be set.");
		List<FilterPlugin> plugins = SpringUtils.orderedBeans(applicationContext, FilterPlugin.class);
		PluginFilter filter = PluginFilter.getInstance(servletContext, filterName);
		if (filter != null) {
			filter.setPlugins(plugins);	
		}
		else {
			PluginFilter.setPlugins(servletContext, filterName, plugins);
		}
	}
	
	public final void destroy() throws Exception {
		PluginFilter filter = PluginFilter.getInstance(servletContext, filterName);
		if (filter != null) {
			filter.setPlugins(null);
		}
	}
}

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
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.web.filter;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;
import org.springframework.web.context.ServletContextAware;

/**
 * Class that can be plugged into a {@link FilterPlugin} to filter request
 * and response objects like a {@link javax.servlet.Filter servlet filter}.
 * <p>
 * Note that plugins are only invoked once per request, so they won't be 
 * called for included or forwarded requests.
 * </p>
 * @author Felix Gnass <fgnass@neteye.de>
 * @since 6.4
 */
public abstract class FilterPlugin implements ServletContextAware, 
		InitializingBean, DisposableBean, Ordered {

	private ServletContext servletContext;
	
	private PluginFilter filter;
	
	private String filterName;
	
	private int order = Integer.MAX_VALUE;
	
	/**
	 * Sets the name of the filter where the plugin should be registered.
	 */
	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}

	/**
	 * Subclasses must implement this method to filter the request/response.
	 * The contract is the same as for {@link javax.servlet.FilterFilter#doFilter}.
	 */
	public abstract void doFilter(HttpServletRequest request, 
			HttpServletResponse response, PluginChain pluginChain)
			throws IOException, ServletException;
	
	/**
	 * Returns the order in which this plugin will be invoked.
	 */
	public int getOrder() {
		return order;
	}
	
	/**
	 * Sets the order in which this plugin will be invoked. The default 
	 * value is <code>Integer.MAX_VALE</code>.
	 */
	public void setOrder(int order) {
		this.order = order;
	}
	
	public final void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	/**
	 * Returns the ServletContext.
	 */
	protected final ServletContext getServletContext() {
		return servletContext;
	}
	
	/**
	 * Retrieves the {@link PluginFilter} with the specified name from the
	 * ServletContext and registers the plugin.
	 */
	public final void afterPropertiesSet() throws Exception {
		Assert.notNull(filterName, "A filterName must be set.");
		filter = PluginFilter.getInstance(servletContext, filterName);
		Assert.notNull(filter, "No such filter: " + filterName);
		initPlugin();
		filter.addPlugin(this);
	}
	
	/**
	 * Subclasses may override this to perform custom initialization. 
	 * All properties of this plugin will have been set before this method 
	 * is invoked. The default implementation is empty.
	 */
	protected void initPlugin() {
	}

	/**
	 * Removes the plugin from the filter and invokes {@link #destroyPlugin()}.
	 */
	public final void destroy() throws Exception {
		filter.removePlugin(this);
		destroyPlugin();
	}
	
	/**
	 * Subclasses may override this to perform custom cleanup operations
	 * when the bean is destroyed. The default implementation is empty.
	 */
	protected void destroyPlugin() {
	}

}

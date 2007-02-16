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
package org.riotfamily.common.web.servlet;

import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.xml.BeanConfigurationWatcher;
import org.riotfamily.common.xml.ConfigurableBean;
import org.springframework.beans.BeansException;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * DispatcherServlet that checks whether one of the configuration files has
 * been modified. If a change is detected the servlet is re-initalized and the
 * underlying BeanFactory is refreshed.
 * <p>
 * As checks are performed upon each request you might want to set the 
 * <code>reloadable</code> init parameter to <code>false</code> when used in
 * a production environment. Alternatively you can add a 
 * {@link ReloadableDispatcherServletConfig} bean to your ApplicationContext
 * which allows you to set the <code>reloadable</code> property without
 * modifying the web.xml.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 * @author Jan-Frederic Linde <jfl@neteye.de>
 */
public class ReloadableDispatcherServlet extends DispatcherServlet 
		implements ConfigurableBean {

	private boolean reloadable = true;

	private ResourceAwareContext context;
	
	private BeanConfigurationWatcher watcher = new BeanConfigurationWatcher(this);
	
	public void setReloadable(boolean reloadable) {
		this.reloadable = reloadable;
	}
	
	public boolean isReloadable() {
		return reloadable;
	}

	protected WebApplicationContext createWebApplicationContext(
			WebApplicationContext parent) throws BeansException {

		context = new ResourceAwareContext();
		
		String[] locations = StringUtils.tokenizeToStringArray(
				getContextConfigLocation(), 
				ConfigurableWebApplicationContext.CONFIG_LOCATION_DELIMITERS);

		context.setParent(parent);
		context.setServletContext(getServletContext());
		context.setServletConfig(getServletConfig());
		context.setNamespace(getNamespace());
		if (getContextConfigLocation() != null) {
			context.setConfigLocations(locations);
		}
		context.refresh();

		Map beansOfType = context.getBeansOfType(
					ReloadableDispatcherServletConfig.class);
		if (beansOfType.size() == 1) {
			ReloadableDispatcherServletConfig config = 
				(ReloadableDispatcherServletConfig) beansOfType.values().
						iterator().next();
			setReloadable(config.isReloadable());
		}		
		
		watcher.setResources(context.getConfigResources());
		return context;
	}
	
	protected void doDispatch(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {

		watcher.checkForModifications();
		super.doDispatch(request, response);
	}
	
	public void configure() {
		context.refresh();
		watcher.setResources(context.getConfigResources());
		try {
			initFrameworkServlet();
		}
		catch (ServletException e) {
			throw new RuntimeException(e);
		}
	}
}

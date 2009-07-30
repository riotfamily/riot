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
package org.riotfamily.common.beans.factory;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * Convenience class that loads a child XmlWebApplicationContext. The loading
 * can be skipped by setting {@link #setLoadContext(boolean)} to false.
 * <p>
 * Use this class if you need to perform setup operations in your development
 * environment, like the population of an in-memory database. 
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class ChildContextLoader implements InitializingBean, 
		ApplicationContextAware, ServletContextAware, BeanNameAware {

	private String displayName;
	
	private boolean loadContext = true;
	
	private String[] configLocations;
	
	private XmlWebApplicationContext context;
	
	private ServletContext servletContext;
	
	private ApplicationContext parent;
	
	public void setConfigLocations(String[] configLocations) {
		this.configLocations = configLocations;
	}

	public void setLoadContext(boolean loadContext) {
		this.loadContext = loadContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.parent = applicationContext;
	}
	
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public void setBeanName(String name) {
		this.displayName = name;
	}
	
	public void afterPropertiesSet() throws Exception {
		if (loadContext) {
			context = new XmlWebApplicationContext();
			context.setParent(parent);
			context.setDisplayName(displayName);
			context.setConfigLocations(configLocations);
			context.setServletContext(servletContext);
			context.refresh();
		}
	}

	public ApplicationContext getContext() {
		return this.context;
	}
	
}

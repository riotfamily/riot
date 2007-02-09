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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.web.util;

import java.io.File;
import java.lang.reflect.Method;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.ServletContextAware;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class ContextPathProvider implements ServletContextAware, 
		InitializingBean {

	public static final String CONTEXT_PATH_ATTRIBUTE = "contextPath";
	
	private static final Log log = LogFactory.getLog(ContextPathProvider.class);
	
	private ServletContext servletContext;
	
	private String contextPath;
	
	private boolean defaultToEmptyPath = false;
	
	/**
	 * Sets whether an empty context path should be assumed, if it can not be
	 * determined. If set to <code>false</code> (default), an exception will
	 * be thrown if the lookup fails.
	 */
	public void setDefaultToEmptyPath(boolean defaultToEmptyPath) {
		this.defaultToEmptyPath = defaultToEmptyPath;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	public ServletContext getServletContext() {
		return servletContext;
	}
	
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(servletContext, "The servletContext must be set.");

		Method method = ReflectionUtils.findMethod(ServletContext.class, 
				"getContextPath", new Class[0]);
		
		if (method != null) {
			log.info("Servlet API 2.5+ detected - using ServletContext.getContextPath()");
			contextPath = (String) ReflectionUtils.invokeMethod(
					method, servletContext);
		}
		else {
			contextPath = (String) servletContext.getAttribute(CONTEXT_PATH_ATTRIBUTE);
			if (contextPath == null) {
				log.info("Servlet API < 2.5 and attibute '" 
						+ CONTEXT_PATH_ATTRIBUTE + "' is not set. Guessing "
						+ "contextPath from real path ...");
				
				String rootPath = servletContext.getRealPath("/");
				if (rootPath != null) {
					File f = new File(rootPath);
					contextPath = f.getName();
					if (contextPath.equals("ROOT")) {
						contextPath = "";
					}
				}
				else {
					if (defaultToEmptyPath) {
						log.info("Unable to determine the context path - "
								+ "assuming an empty path.");
					}
					else {
						log.fatal("Unable to determine real path for '/'. "
								+ "The WAR file is possibly not expanded");
						
						throw new FatalBeanException("The context attribute '" 
								+ CONTEXT_PATH_ATTRIBUTE + "' must be set.");
					}
				}
			}
		}
	}
	
	public String getContextPath() {
		return this.contextPath;
	}
	
}

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
 * Portions created by the Initial Developer are Copyright (C) 2008
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.web.servlet;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.web.util.ServletUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.web.context.ServletContextAware;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class WebXmlPathCompleter extends DefaultPathCompleter 
		implements ServletContextAware, InitializingBean {

	private static final Log log = LogFactory.getLog(WebXmlPathCompleter.class);

	private String servletName;

	private String servletMapping;

	private ServletContext servletContext;

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public void setServletMapping(String servletMapping) {
		this.servletMapping = servletMapping;
	}

	public void setServletName(String servletName) {
		this.servletName = servletName;
	}

	public void afterPropertiesSet() throws Exception {
		if (servletMapping == null) {
			Assert.notNull(servletName, "Either servletMapping or "
					+ "servletName must be set.");

			servletMapping = ServletUtils.getServletMapping(servletName,
					servletContext);

			Assert.notNull(servletMapping, "Could not determine mapping "
					+ "for servlet '" + servletName + "'.");

			log.info("Servlet '" + servletName + "' is mapped to "
					+ servletMapping + " in web.xml");
		}
				
		int i = servletMapping.indexOf('*');
		if (i == 0) {
			setServletSuffix(servletMapping.substring(1));
		}
		if (i > 0) {
			setServletPrefix(servletMapping.substring(0, i - 1));
		}
	}
	
}

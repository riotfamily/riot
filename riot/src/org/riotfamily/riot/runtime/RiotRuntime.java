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
package org.riotfamily.riot.runtime;

import javax.servlet.ServletContext;

import org.riotfamily.riot.RiotVersion;
import org.springframework.util.Assert;
import org.springframework.web.context.ServletContextAware;

/**
 * Bean that exposes the riot-servlet prefix, the resource path and the
 * riot version.
 * <p>
 * By default, Riot assumes that the riot-servlet is mapped to 
 * <code>/riot/*</code>. In order to use a different mapping, you have to set 
 * the context attribute <code>riotServletPrefix</code> in your 
 * <code>web.xml</code>.
 * </p>
 */
public class RiotRuntime implements ServletContextAware {

	public static final String SERVLET_PREFIX_ATTRIBUTE = "riotServletPrefix";
	
	public static final String DEFAULT_SERVLET_PREFIX = "/riot";
	
	private String servletPrefix;
	
	private String resourceMapping;

	private String resourcePath;

	public void setResourceMapping(String resourceMapping) {
		this.resourceMapping = resourceMapping;
	}

	public void setServletContext(ServletContext context) {
		Assert.notNull(resourceMapping, "A resourceMapping must be specified.");
		servletPrefix = (String) context.getAttribute(SERVLET_PREFIX_ATTRIBUTE);
		if (servletPrefix == null) {
			servletPrefix = DEFAULT_SERVLET_PREFIX;
		}
		resourcePath = servletPrefix + resourceMapping + '/' + getVersionString();
	}
	
	public String getServletPrefix() {
		return servletPrefix;
	}
	
	public String getResourcePath() {
		return resourcePath;
	}
    
	public String getVersionString() {
		return RiotVersion.getVersionString();
	}

}

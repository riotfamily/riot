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
package org.riotfamily.pages.freemarker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.riotfamily.common.web.util.IncludeFirstInterceptor;
import org.riotfamily.common.web.view.freemarker.EncodeUrlMethod;
import org.riotfamily.common.web.view.freemarker.ErrorPrintingExceptionHandler;
import org.riotfamily.common.web.view.freemarker.IncludeMethod;
import org.riotfamily.pages.component.preview.ViewModeResolver;
import org.riotfamily.riot.runtime.RiotRuntime;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 * FreeMarkerConfigurer that exposes all variables needed by the Riot toolbar
 * and auto-imports the Riot macros under the namespace 'riot'.
 */
public class FreeMarkerConfigurer extends
		org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer
		implements ApplicationContextAware {

	public static final String RIOT_NAMESPACE = "riot";
	
	private static final String RIOT_MACROS = "/riot.ftl";
	
	private static final String RESOURCES_JS = "/riot-js/resources.js";
	
	private static final String VAR_TOOLBAR_RESOURCES = "riotToolbarResources";
	
	private static final String VAR_SERVLET_PREFIX = "riotServletPrefix";	
	
	private static final String VAR_INCLUDE_URI_PARAM = "includeUriParam";
	
	private static final String VAR_VIEW_MODE_RESOLVER = "viewModeResolver";
	
	private static final String VAR_ENCODE_URL_METHOD = "riotEncodeUrl";
	
	private static final String VAR_INCLUDE_METHOD = "riotInclude";
	
	private static final TemplateExceptionHandler DEFAULT_EXCEPTION_HANDLER =
			new ErrorPrintingExceptionHandler();
	
	private String riotServletPrefix;
	
	private List riotToolbarResources;
	
	private ViewModeResolver viewModeResolver;
	
	public void setApplicationContext(ApplicationContext context) 
			throws BeansException {
		
		RiotRuntime runtime = (RiotRuntime) BeanFactoryUtils
				.beanOfTypeIncludingAncestors(context, RiotRuntime.class);
				
		riotServletPrefix = runtime.getServletPrefix();
		String riotResourcePath = runtime.getResourcePath();
		List resources = (List) context.getBean(VAR_TOOLBAR_RESOURCES, List.class);
		
		riotToolbarResources = new ArrayList(resources.size() + 1);
		riotToolbarResources.add(riotResourcePath + RESOURCES_JS);
		Iterator it = resources.iterator();
		while(it.hasNext()) {
			String resource = (String) it.next();
			if (!resource.startsWith(riotResourcePath)) {
				resource = riotResourcePath + resource;					
			}
			riotToolbarResources.add(resource);
		}		
		
		viewModeResolver = (ViewModeResolver) BeanFactoryUtils.beanOfType(
				context, ViewModeResolver.class);
	}
	
	protected void postProcessTemplateLoaders(List templateLoaders) {
		// We can't call super method, because it uses getClass() ...
		templateLoaders.add(new ClassTemplateLoader(org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer.class, ""));
		templateLoaders.add(new ClassTemplateLoader(FreeMarkerConfigurer.class, ""));
		logger.info("ClassTemplateLoaders for Spring and Riot macros added to " 
				+ "FreeMarker configuration");
	}
	
	/**
	 * Calls <code>super.newConfiguration()</code> and sets a 
	 * {@link TemplateExceptionHandler} that does not print the stacktrace. 
	 * @since 6.4
	 */
	protected Configuration newConfiguration() throws IOException, TemplateException {
		Configuration configuration = super.newConfiguration();
		configuration.setTemplateExceptionHandler(DEFAULT_EXCEPTION_HANDLER);
		return configuration;
	}
	
	protected void postProcessConfiguration(Configuration configuration) 
			throws IOException, TemplateException {
		
		configuration.addAutoImport(RIOT_NAMESPACE, RIOT_MACROS);
		logger.info("Riot macros imported under namespace " + RIOT_NAMESPACE);
		
		SimpleHash vars = new SimpleHash();
		vars.put(VAR_SERVLET_PREFIX, riotServletPrefix);
		vars.put(VAR_TOOLBAR_RESOURCES, riotToolbarResources);
		vars.put(VAR_VIEW_MODE_RESOLVER, viewModeResolver);
		vars.put(VAR_ENCODE_URL_METHOD, new EncodeUrlMethod());
		vars.put(VAR_INCLUDE_METHOD, new IncludeMethod());
		vars.put(VAR_INCLUDE_URI_PARAM, IncludeFirstInterceptor.INCLUDE_URI_PARAM);
		configuration.setAllSharedVariables(vars);
	}
	
}

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

/**
 * FreeMarkerConfigurer that exposes all variables needed by the Riot toolbar
 * and auto-imports the Riot macros under the namespace 'riot'.
 */
public class FreeMarkerConfigurer extends
		org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer
		implements ApplicationContextAware {

	public static final String RIOT_NAMESPACE = "riot";
	
	private static final String RIOT_MACRO_TEMPLATE = "/riot.ftl";
	
	private static final String RIOT_RESOURCES_JS = "/riot-js/resources.js";
	
	private static final String RIOT_RUNTIME_BEAN_NAME = "riotRuntime";
	
	private static final String RIOT_TOOLBAR_RESOURCES = "riotToolbarResources";
	
	private static final String SERVLET_PREFIX_VAR = "riotServletPrefix";	
	
	private static final String INCLUDE_URI_PARAM_VAR = "includeUriParam";
	
	private static final String VIEW_MODE_RESOLVER_VAR = "viewModeResolver";
	
	private static final String ENCODE_URL_METHOD_VAR = "riotEncodeUrl";
	
	private static final String INCLUDE_METHOD_VAR = "riotInclude";
	
	
	private String riotServletPrefix;
	
	private List riotToolbarResources;
	
	private ViewModeResolver viewModeResolver;
	
	public void setApplicationContext(ApplicationContext context) 
			throws BeansException {
		
		RiotRuntime runtime = (RiotRuntime) context.getBean(
				RIOT_RUNTIME_BEAN_NAME, RiotRuntime.class);
		
		riotServletPrefix = runtime.getServletPrefix();
		String riotResourcePath = runtime.getResourcePath();
		List resources = (List) 
				context.getBean(RIOT_TOOLBAR_RESOURCES, List.class);
		
		riotToolbarResources = new ArrayList(resources.size() + 1);
		riotToolbarResources.add(riotResourcePath + RIOT_RESOURCES_JS);
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
	
	protected void postProcessConfiguration(Configuration configuration) 
			throws IOException, TemplateException {
		
		super.postProcessConfiguration(configuration);

		configuration.addAutoImport(RIOT_NAMESPACE, RIOT_MACRO_TEMPLATE);
		logger.info("Riot macros imported under namespace " + RIOT_NAMESPACE);
		
		SimpleHash vars = new SimpleHash();
		vars.put(SERVLET_PREFIX_VAR, riotServletPrefix);
		vars.put(RIOT_TOOLBAR_RESOURCES, riotToolbarResources);
		vars.put(VIEW_MODE_RESOLVER_VAR, viewModeResolver);
		vars.put(ENCODE_URL_METHOD_VAR, new EncodeUrlMethod());
		vars.put(INCLUDE_METHOD_VAR, new IncludeMethod());
		vars.put(INCLUDE_URI_PARAM_VAR, IncludeFirstInterceptor.INCLUDE_URI_PARAM);
		configuration.setAllSharedVariables(vars);
	}
	
}

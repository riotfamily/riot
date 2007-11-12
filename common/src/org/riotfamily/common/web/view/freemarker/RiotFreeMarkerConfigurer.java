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
package org.riotfamily.common.web.view.freemarker;

import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class RiotFreeMarkerConfigurer extends FreeMarkerConfigurer 
		implements ApplicationContextAware {

	private static final Log log = LogFactory
			.getLog(RiotFreeMarkerConfigurer.class);
	
	private TemplateExceptionHandler exceptionHandler = 
			new ErrorPrintingExceptionHandler();
	
	private Properties macroLibraries;
	
	private ObjectWrapper objectWrapper;
	
	private Map sharedVariables;
		
	private boolean whitespaceStripping = false;
	
	private boolean useTemplateCache = true;
	
	private int templateUpdateDelay = 5;
	
	private String urlEscapingCharset = "UTF-8";

	private ApplicationContext applicationContext;
	
	
	/**
	 * Sets the macro libraries to be auto-imported, keyed by their namespace.
	 */
	public void setMacroLibraries(Properties macroLibraries) {
		this.macroLibraries = macroLibraries;
	}

	/**
	 * Sets the {@link TemplateExceptionHandler} to be used. By default an
	 * {@link ErrorPrintingExceptionHandler} will be used. 
	 */
	public void setExceptionHandler(TemplateExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}
	
	/**
	 * Sets the {@link ObjectWrapper} to be used. If <code>null</code> 
	 * (which is the default), FreeMarker's DefaultObjectWrapper will be used. 
	 */
	public void setObjectWrapper(ObjectWrapper objectWrapper) {
		this.objectWrapper = objectWrapper;
	}

	/**
	 * Set a Map that contains well-known FreeMarker objects which will be passed
	 * to FreeMarker's <code>Configuration.setAllSharedVariables()</code> method.
	 * <p>
	 * Riot overrides this setter in order to set the variables in 
	 * {@link #postProcessConfiguration(Configuration)}, after the custom
	 * {@link ObjectWrapper} has been set.
	 * </p>
	 * @see freemarker.template.Configuration#setAllSharedVariables
	 */
	public void setFreemarkerVariables(Map variables) {
		sharedVariables = variables;
	}
	
	/**
	 * Sets whether the FTL parser will try to remove superfluous
	 * white-space around certain FTL tags.
	 */
	public void setWhitespaceStripping(boolean whitespaceStripping) {
		this.whitespaceStripping = whitespaceStripping;
	}
	
	/**
	 * Sets the URL escaping charset. Allows null, which means that the 
	 * output encoding will be used for URL escaping.
	 * Default is <code>UTF-8</code>.
	 */
	public void setUrlEscapingCharset(String urlEscapingCharset) {
		this.urlEscapingCharset = urlEscapingCharset;
	}
	
	/**
	 * Sets whether the FreeMarker template cache should be used 
	 * (default is <code>true</code>).
	 */
	public void setUseTemplateCache(boolean useTemplateCache) {
		this.useTemplateCache = useTemplateCache;
	}
	
	/**
	 * Set the time in seconds that must elapse before checking whether there 
	 * is a newer version of a template file. Default is <code>5</code>.
	 */
	public void setTemplateUpdateDelay(int templateUpdateDelay) {
		this.templateUpdateDelay = templateUpdateDelay;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	public void afterPropertiesSet() throws IOException, TemplateException {
		if (objectWrapper == null) {
			Collection plugins = applicationContext.getBeansOfType(
					ObjectWrapperPlugin.class).values();
			
			if (!plugins.isEmpty()) {
				objectWrapper = new PluginObjectWrapper(plugins);
			}
		}
		super.afterPropertiesSet();
	}
	
	protected void postProcessTemplateLoaders(List templateLoaders) {
		super.postProcessTemplateLoaders(templateLoaders);
		templateLoaders.add(new ResourceTemplateLoader(getResourceLoader()));
	}
	
	protected void postProcessConfiguration(Configuration config) 
			throws IOException, TemplateException {
		
		config.setURLEscapingCharset(urlEscapingCharset);
		config.setWhitespaceStripping(whitespaceStripping);
		importMacroLibraries(config);
		config.setTemplateExceptionHandler(exceptionHandler);
		if (objectWrapper != null) {
			config.setObjectWrapper(objectWrapper);
		}
		
		if (sharedVariables != null) {
			config.setAllSharedVariables(
					new SimpleHash(sharedVariables, objectWrapper));
		}
		
		if (useTemplateCache) {
			config.setTemplateUpdateDelay(templateUpdateDelay);
		}
		else {
			config.setCacheStorage(new NoCacheStorage());
		}
	}
	
	protected void importMacroLibraries(Configuration config) {
		if (macroLibraries != null) {
			Enumeration names = macroLibraries.propertyNames();
			while (names.hasMoreElements()) {
				String namespace = (String) names.nextElement();
				String lib = macroLibraries.getProperty(namespace);
				log.info(lib + " imported under namespace " + namespace);
				config.addAutoImport(namespace, lib);	
			}
		}
	}
	
}

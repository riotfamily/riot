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
package org.riotfamily.common.freemarker;

import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.riotfamily.common.util.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateModel;

/**
 * FreeMarkerConfigurer that supports some additional settings. 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class RiotFreeMarkerConfigurer extends FreeMarkerConfigurer 
		implements ApplicationContextAware {

	private Logger log = LoggerFactory.getLogger(RiotFreeMarkerConfigurer.class);
	
	private TemplateExceptionHandler exceptionHandler = 
			new ErrorPrintingExceptionHandler();
	
	private Properties macroLibraries;
	
	private Map<String, ?> sharedVariables;
		
	private boolean whitespaceStripping = false;
	
	private boolean useTemplateCache = true;
	
	private boolean useComputerNumberFormat = true;
	
	private boolean exposeStaticModels = true;
	
	private boolean exposeBeanFactoryModel = true;
	
	private int templateUpdateDelay = 5;
	
	private String urlEscapingCharset = "UTF-8";

	private ApplicationContext applicationContext;

	private ObjectWrapper objectWrapper;
	
	
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
	 * Set a Map that contains well-known FreeMarker objects which will be passed
	 * to FreeMarker's <code>Configuration.setAllSharedVariables()</code> method.
	 * <p>
	 * Riot overrides this setter in order to set the variables in 
	 * {@link #postProcessConfiguration(Configuration)}, after the custom
	 * {@link ObjectWrapper} has been set.
	 * </p>
	 * @see freemarker.template.Configuration#setAllSharedVariables
	 */
	@Override
	@SuppressWarnings("unchecked")
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
	 * Whether the <code>#0.#</code> should be used as default number format.
	 * Default is <code>true</code>.
	 */
	public void setUseComputerNumberFormat(boolean useComputerNumberFormat) {
		this.useComputerNumberFormat = useComputerNumberFormat;
	}
	
	/**
	 * Whether {@link BeansWrapper#getStaticModels()} should be exposed as
	 * <tt>statics</tt>.
	 */
	public void setExposeStaticModels(boolean exposeStaticModels) {
		this.exposeStaticModels = exposeStaticModels;
	}
	
	/**
	 * Whether a {@link BeanFactoryTemplateModel} should be exposed as
	 * <tt>beans</tt>.
	 */
	public void setExposeBeanFactoryModel(boolean exposeBeanFactoryModel) {
		this.exposeBeanFactoryModel = exposeBeanFactoryModel;
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

	public void setObjectWrapper(ObjectWrapper objectWrapper) {
		this.objectWrapper = objectWrapper;
	}
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected void postProcessTemplateLoaders(List templateLoaders) {
		super.postProcessTemplateLoaders(templateLoaders);
		templateLoaders.add(new ResourceTemplateLoader(getResourceLoader()));
	}
	
	@Override
	protected void postProcessConfiguration(Configuration config) 
			throws IOException, TemplateException {
		
		config.setURLEscapingCharset(urlEscapingCharset);
		config.setWhitespaceStripping(whitespaceStripping);
		if (useComputerNumberFormat) {
			config.setNumberFormat("#0.#");
		}
		
		importMacroLibraries(config);
		config.setTemplateExceptionHandler(exceptionHandler);
		if (objectWrapper == null) {
			objectWrapper = DefaultObjectWrapper.getDefaultInstance();
		}
		config.setObjectWrapper(objectWrapper);
		
		if (sharedVariables != null) {
			config.setAllSharedVariables(
					new SimpleHash(sharedVariables, objectWrapper));
		}
		
		if (exposeStaticModels) {
			config.setSharedVariable("statics", getStaticsModel(objectWrapper));
		}

		if (exposeBeanFactoryModel) {
			config.setSharedVariable("beans", new BeanFactoryTemplateModel(
					applicationContext, objectWrapper));
		}
		
		if (useTemplateCache) {
			config.setTemplateUpdateDelay(templateUpdateDelay);
		}
		else {
			config.setCacheStorage(new NoCacheStorage());
		}
		
		Collection<ConfigurationPostProcessor> postProcessors = 
				SpringUtils.orderedBeans(applicationContext, 
						ConfigurationPostProcessor.class);
		
		for (ConfigurationPostProcessor postProcessor : postProcessors) {
			postProcessor.postProcessConfiguration(config);
		}
		
	}
	
	private TemplateModel getStaticsModel(ObjectWrapper wrapper) {
		if (wrapper instanceof BeansWrapper) {
			return ((BeansWrapper) wrapper).getStaticModels();
		}
		return BeansWrapper.getDefaultInstance().getStaticModels();
	}

	protected void importMacroLibraries(Configuration config) {
		if (macroLibraries != null) {
			Enumeration<?> names = macroLibraries.propertyNames();
			while (names.hasMoreElements()) {
				String namespace = (String) names.nextElement();
				String lib = macroLibraries.getProperty(namespace);
				log.info(lib + " imported under namespace " + namespace);
				config.addAutoImport(namespace, lib);	
			}
		}
	}
	
}

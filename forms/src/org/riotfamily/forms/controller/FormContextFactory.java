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
package org.riotfamily.forms.controller;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.i18n.AdvancedMessageCodesResolver;
import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.common.i18n.RiotMessageCodesResolver;
import org.riotfamily.common.util.Generics;
import org.riotfamily.common.util.SpringUtils;
import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.common.web.view.freemarker.ResourceTemplateLoader;
import org.riotfamily.forms.FormContext;
import org.riotfamily.forms.OptionsModelAdapter;
import org.riotfamily.forms.TemplateRenderer;
import org.riotfamily.forms.options.ArrayOptionsModelAdapter;
import org.riotfamily.forms.options.CollectionOptionsModelAdapter;
import org.riotfamily.forms.options.DependentOptionsModelAdapter;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.servlet.support.RequestContextUtils;

import freemarker.template.Configuration;

public final class FormContextFactory implements MessageSourceAware, 
		ResourceLoaderAware, BeanFactoryAware, InitializingBean {
	
	private MessageSource messageSource;

	private ResourceLoader resourceLoader;
	
	private AdvancedMessageCodesResolver messageCodesResolver;

	private TemplateRenderer templateRenderer;
	
	private String resourcePath = "/riot/resources";

	private Collection<PropertyEditorRegistrar> propertyEditorRegistrars;
	
	private List<OptionsModelAdapter> optionValuesAdapters = Generics.newArrayList();

	public FormContextFactory() {
		registerDefaultOptionValuesAdapters();
	}
	
	/**
	 * Sets the {@link AdvancedMessageCodesResolver} to be used. If not
	 * set, a {@link RiotMessageCodesResolver} will be used by default.
	 */
	public void setMessageCodesResolver(AdvancedMessageCodesResolver resolver) {
		this.messageCodesResolver = resolver;
	}
	
	/**
	 * Sets the {@link MessageSource} that is used to look up labels and
	 * error messages.
	 * 
	 * @see MessageSourceAware
	 */
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	/**
	 * Sets the {@link ResourceLoader} that is used to load the FreeMarker
	 * templates.
	 * 
	 * @see ResourceLoaderAware
	 */
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}
	
	@SuppressWarnings("unchecked")
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		if (beanFactory instanceof AbstractBeanFactory) {
			AbstractBeanFactory abf = (AbstractBeanFactory) beanFactory;
			this.propertyEditorRegistrars = abf.getPropertyEditorRegistrars();
		}
		if (beanFactory instanceof ListableBeanFactory) {
			ListableBeanFactory lbf = (ListableBeanFactory) beanFactory;
			optionValuesAdapters.addAll(SpringUtils.beansOfType(
					lbf, OptionsModelAdapter.class).values());			
		}
	}
	
	private void registerDefaultOptionValuesAdapters() {
		optionValuesAdapters.add(new CollectionOptionsModelAdapter());
		optionValuesAdapters.add(new ArrayOptionsModelAdapter());
		optionValuesAdapters.add(new DependentOptionsModelAdapter());
	}
	
	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}
	
	public void setTemplateRenderer(TemplateRenderer templateRenderer) {
		this.templateRenderer = templateRenderer;
	}
	
	public void afterPropertiesSet() {
		if (templateRenderer == null) {
			Configuration configuration = new Configuration();
			ResourceTemplateLoader loader = new ResourceTemplateLoader();
			loader.setResourceLoader(resourceLoader);
			configuration.setTemplateLoader(loader);
			templateRenderer = new TemplateRenderer(configuration);
		}
		if (messageCodesResolver == null) {
			messageCodesResolver = new RiotMessageCodesResolver();
		}
	}
	
	public MessageResolver getMessageResolver(Locale locale) {
		return new MessageResolver(messageSource, messageCodesResolver, locale);
	}
	public FormContext createFormContext(HttpServletRequest request, 
			HttpServletResponse response) {

		Locale locale = RequestContextUtils.getLocale(request);
		MessageResolver messageResolver = getMessageResolver(locale);
		
		String contextPath = request.getContextPath();
		String formUrl = ServletUtils.getOriginatingRequestUri(request);
		return createFormContext(messageResolver, contextPath, formUrl);
	}
	
	public FormContext createFormContext(MessageResolver messageResolver,
			String contextPath, String formUrl) {
		
		return new DefaultFormContext(messageResolver, templateRenderer, 
				contextPath, resourcePath, formUrl,
				propertyEditorRegistrars, optionValuesAdapters);
	}

}

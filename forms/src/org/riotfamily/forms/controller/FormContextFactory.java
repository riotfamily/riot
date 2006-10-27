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
package org.riotfamily.forms.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.i18n.AdvancedMessageCodesResolver;
import org.riotfamily.common.i18n.RiotMessageCodesResolver;
import org.riotfamily.common.web.view.freemarker.ResourceTemplateLoader;
import org.riotfamily.forms.FormContext;
import org.riotfamily.forms.template.TemplateRenderer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;

import freemarker.template.Configuration;

public final class FormContextFactory implements MessageSourceAware, 
		ResourceLoaderAware, InitializingBean {
	
	private MessageSource messageSource;

	private ResourceLoader resourceLoader;
	
	private AdvancedMessageCodesResolver messageCodesResolver;

	private TemplateRenderer templateRenderer;
	
	private String resourcePath = "/riot/resources";
	
	/**
	 * Sets the {@link AdvancedMessageCodesResolver} to be used. If not
	 * set, a {@link RiotMessageCodesResolver} will be used by default.
	 */
	public void setMessageCodesResolver(AdvancedMessageCodesResolver resolver) {
		this.messageCodesResolver = resolver;
	}

	/**
	 * Sets the {@link MessageSource} that is used to look up lables and
	 * error messages.
	 * 
	 * @see MessageSourceAware
	 */
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	/**
	 * Sets the {@link ResourceLoader} that is used to load the Freemarker
	 * templates.
	 * 
	 * @see ResourceLoaderAware
	 */
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
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
	
	public FormContext createFormContext(HttpServletRequest request, 
			HttpServletResponse response) {

		return new FormContext(messageCodesResolver, messageSource, 
				request, response, resourcePath, templateRenderer);
	}

}

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

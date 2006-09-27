package org.riotfamily.forms;

import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.i18n.AdvancedMessageCodesResolver;
import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.forms.element.ContentElement;
import org.riotfamily.forms.template.TemplateRenderer;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.support.RequestContextUtils;

public final class FormContext {
	
	private String contextPath;
	
	private String resourcePath;
	
	/** Renderer for templates */
	private TemplateRenderer templateRenderer;

	/** Locale to use for messages */
	private Locale locale;
	
	private MessageSource messageSource;
	
	private MessageResolver messageResolver; 
	
	/** Current writer used for rendering */ 
	private PrintWriter writer;

	private String formUrl;
	
	private PropertyEditorRegistrar[] propertyEditorRegistrars;
	
	public FormContext() {
	}
	
	public FormContext(AdvancedMessageCodesResolver messageCodesResolver, 
			MessageSource messageSource, HttpServletRequest request, 
			HttpServletResponse response, String resourcePath, 
			TemplateRenderer templateRenderer) {

		this.messageSource = messageSource;
		this.locale = RequestContextUtils.getLocale(request);
		this.messageResolver = new MessageResolver(
				messageSource, messageCodesResolver, locale);
				 
		this.contextPath = request.getContextPath();
		setResourcePath(resourcePath);
		this.templateRenderer = templateRenderer;
		this.formUrl = response.encodeURL(ServletUtils.getIncludeUri(request));
	}

	private void setResourcePath(String path) {
		if (path != null && path.length() > 0) {
			if (path.charAt(path.length() - 1) != '/') {
				path += '/';
			}
		}
		else {
			path = "/";
		}
		this.resourcePath = path;
	}
	
	public Locale getLocale() {
		return locale;
	}
	
	public MessageSource getMessageSource() {
		return messageSource;
	}

	public MessageResolver getMessageResolver() {
		return messageResolver;
	}
	
	public String getContextPath() {
		return contextPath;
	}
	
	public String getResourcePath() {
		return resourcePath;
	}

	public TemplateRenderer getTemplateRenderer() {
		return templateRenderer;
	}

	public PrintWriter getWriter() {
		return writer;
	}

	public void setWriter(PrintWriter writer) {
		this.writer = writer;
	}
	
	public String getFormUrl() {
		return formUrl;
	}
	
	private StringBuffer createFormUrlBuffer() {
		StringBuffer url = new StringBuffer(formUrl);
		if (url.indexOf("?") != -1) {
			url.append('&');
		}
		else {
			url.append('?');
		}
		return url;
	}
	
	public String getContentUrl(ContentElement el) {
		return createFormUrlBuffer()
			.append("_content=" + el.getId())
			.toString();
	}
	
	public String getUploadUrl(String uploadId) {
		return createFormUrlBuffer().append("uploadId=" + uploadId).toString();
	}

	public PropertyEditorRegistrar[] getPropertyEditorRegistrars() {
		return this.propertyEditorRegistrars;
	}

	public void setPropertyEditorRegistrars(
			PropertyEditorRegistrar[] registrars) {
		
		this.propertyEditorRegistrars = registrars;
	}
	
}

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
 *   flx
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.forms.controller;

import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;

import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.forms.ContentElement;
import org.riotfamily.forms.FormContext;
import org.riotfamily.forms.OptionsModelFactory;
import org.riotfamily.forms.TemplateRenderer;
import org.springframework.beans.PropertyEditorRegistrar;

/**
 * @author Felix Gnass [fgnass at neteye dot de] 
 * @since 6.4
 */
public class DefaultFormContext implements FormContext {

	private String contextPath;
	
	private String resourcePath;
	
	/** Renderer for templates */
	private TemplateRenderer templateRenderer;

	private MessageResolver messageResolver; 
	
	/** Current writer used for rendering */ 
	private PrintWriter writer;

	private String formUrl;
	
	private PropertyEditorRegistrar[] propertyEditorRegistrars;
	
	private List<OptionsModelFactory> optionValuesAdapters;
	
	public DefaultFormContext() {
	}
	
	public DefaultFormContext(MessageResolver messageResolver,
			TemplateRenderer templateRenderer,
			String contextPath, String resourcePath, String formUrl,
			List<OptionsModelFactory> optionValuesAdapters) {

		this.messageResolver = messageResolver;
		this.contextPath = contextPath;
		setResourcePath(resourcePath);
		this.templateRenderer = templateRenderer;
		this.formUrl = formUrl;
		this.optionValuesAdapters = optionValuesAdapters;
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
		return messageResolver.getLocale();
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

	public List<OptionsModelFactory> getOptionValuesAdapters() {
		return optionValuesAdapters;
	}
	
}

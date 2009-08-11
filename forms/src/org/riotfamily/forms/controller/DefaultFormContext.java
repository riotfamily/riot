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
package org.riotfamily.forms.controller;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.forms.ContentElement;
import org.riotfamily.forms.FormContext;
import org.riotfamily.forms.OptionsModelAdapter;
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
	
	private Collection<PropertyEditorRegistrar> propertyEditorRegistrars;
	
	private List<OptionsModelAdapter> optionValuesAdapters;
	
	public DefaultFormContext() {
	}
	
	public DefaultFormContext(MessageResolver messageResolver,
			TemplateRenderer templateRenderer,
			String contextPath, String resourcePath, String formUrl,
			Collection<PropertyEditorRegistrar> propertyEditorRegistrars,
			List<OptionsModelAdapter> optionValuesAdapters) {

		this.messageResolver = messageResolver;
		this.contextPath = contextPath;
		setResourcePath(resourcePath);
		this.templateRenderer = templateRenderer;
		this.formUrl = formUrl;
		this.propertyEditorRegistrars = propertyEditorRegistrars;
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

	public Collection<PropertyEditorRegistrar> getPropertyEditorRegistrars() {
		return this.propertyEditorRegistrars;
	}

	public void setPropertyEditorRegistrars(
			Collection<PropertyEditorRegistrar> registrars) {
		
		this.propertyEditorRegistrars = registrars;
	}

	public List<OptionsModelAdapter> getOptionsModelAdapters() {
		return optionValuesAdapters;
	}
	
}

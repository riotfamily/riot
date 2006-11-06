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
package org.riotfamily.pages.component.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.pages.component.Component;
import org.riotfamily.pages.component.ComponentList;
import org.riotfamily.pages.component.ComponentListConfiguration;
import org.riotfamily.pages.component.ComponentVersion;
import org.riotfamily.pages.component.property.PropertyProcessor;
import org.riotfamily.pages.component.render.LiveModeRenderStrategy;
import org.riotfamily.pages.component.render.PreviewModeRenderStrategy;
import org.riotfamily.pages.component.render.RenderStrategy;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Component that displays all components from the parent page.
 */
public class InheritingComponent implements Component, MessageSourceAware {

	private Log log = LogFactory.getLog(InheritingComponent.class);
	
	private MessageSource messageSource;
	
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void render(ComponentVersion version, String positionClassName, 
			ComponentListConfiguration config, HttpServletRequest request, 
			HttpServletResponse response) throws IOException {
				
		ComponentList list = version.getContainer().getList();
		String path = list.getPath();
		log.debug("Path: " + path);
		
		String parentPath = config.getComponentPathResolver()
				.getParentPath(path);
		
		log.debug("Parent path: " + parentPath);
		
		if (path != null) {
			if (path.equals(parentPath)) {
				log.warn("Parent path is the same");
				return;
			}
			
			boolean preview = config.getViewModeResolver()
					.isPreviewMode(request);
			
			RenderStrategy strategy = null;
			if (preview) {
				strategy = new InheritingRenderStrategy(
						config, request, response, messageSource);
			}
			else {
				strategy = new LiveModeRenderStrategy(
						config, request, response);
			}
			strategy.render(parentPath, list.getKey());
		}
		else {
			log.warn("No parent path returned by resolver");
		}
	}
	
	public boolean isDynamic() {
		return true;
	}

	public void addPropertyProcessor(PropertyProcessor propertyProcessor) {
	}
	
	public Map buildModel(ComponentVersion version) {
		return null;
	}
	
	public void updateProperties(ComponentVersion version, Map model) {
	}
	
	public List getPropertyProcessors() {		
		return null;
	}
		
	private static class InheritingRenderStrategy extends PreviewModeRenderStrategy {

		private MessageSource messageSource;
		
		public InheritingRenderStrategy(ComponentListConfiguration config, 
				HttpServletRequest request, HttpServletResponse response,
				MessageSource messageSource) throws IOException {
			
			super(config, request, response);
			this.messageSource = messageSource;
		}
		
		protected void renderComponentList(ComponentList list) throws IOException {
			Object editMode = request.getAttribute(EDIT_MODE_ATTRIBUTE);
			request.setAttribute(EDIT_MODE_ATTRIBUTE, Boolean.FALSE);
			super.renderComponentList(list);
			request.setAttribute(EDIT_MODE_ATTRIBUTE, editMode);
		}
		
		
		protected void onListNotFound(String path, String key) throws IOException {
			Locale locale = RequestContextUtils.getLocale(request);
			PrintWriter out = response.getWriter();
			out.print("<div class=\"riot-no-inheritance\">");
			out.print(messageSource.getMessage(
					"pages.inheritingComponent.noParentList", null, 
					"No parent list available", locale));
			
			out.print("</div>");
		}
		
		protected void onEmptyComponentList() throws IOException {
			Locale locale = RequestContextUtils.getLocale(request);
			PrintWriter out = response.getWriter();
			out.print("<div class=\"riot-no-inheritance\">");
			out.print(messageSource.getMessage(
					"pages.inheritingComponent.emptyParentList", null, 
					"The parent list does not contain any components", locale));
			
			out.print("</div>");
		}
		
	}

}

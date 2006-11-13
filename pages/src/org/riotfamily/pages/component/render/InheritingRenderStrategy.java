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
package org.riotfamily.pages.component.render;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.pages.component.ComponentList;
import org.riotfamily.pages.component.ComponentRepository;
import org.riotfamily.pages.component.config.ComponentListConfiguration;
import org.riotfamily.pages.component.dao.ComponentDao;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.support.RequestContextUtils;

public class InheritingRenderStrategy extends PreviewModeRenderStrategy {

	private MessageSource messageSource;
	
	public InheritingRenderStrategy(ComponentDao dao, 
			ComponentRepository repository, ComponentListConfiguration config,
			HttpServletRequest request, HttpServletResponse response) 
			throws IOException {
		
		super(dao, repository, config, request, response);
		this.messageSource = repository.getMessageSource();
	}
	
	protected void renderComponentList(ComponentList list) throws IOException {
		Object editMode = request.getAttribute(EDIT_MODE_ATTRIBUTE);
		request.setAttribute(EDIT_MODE_ATTRIBUTE, Boolean.FALSE);
		super.renderComponentList(list);
		request.setAttribute(EDIT_MODE_ATTRIBUTE, editMode);
	}
	
	
	protected void onListNotFound(String path, String key) throws IOException {
		Locale locale = RequestContextUtils.getLocale(request);
		out.print("<div class=\"riot-no-inheritance\">");
		out.print(messageSource.getMessage(
				"pages.inheritingComponent.noParentList", null, 
				"No parent list available", locale));
		
		out.print("</div>");
	}
	
	protected void onEmptyComponentList() throws IOException {
		Locale locale = RequestContextUtils.getLocale(request);
		out.print("<div class=\"riot-no-inheritance\">");
		out.print(messageSource.getMessage(
				"pages.inheritingComponent.emptyParentList", null, 
				"The parent list does not contain any components", locale));
		
		out.print("</div>");
	}
	
}
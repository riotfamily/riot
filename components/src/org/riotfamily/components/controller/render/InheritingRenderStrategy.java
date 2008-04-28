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
package org.riotfamily.components.controller.render;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.markup.Html;
import org.riotfamily.common.markup.TagWriter;
import org.riotfamily.components.EditModeUtils;
import org.riotfamily.components.config.ComponentListConfiguration;
import org.riotfamily.components.config.ComponentRepository;
import org.riotfamily.components.config.component.ComponentRenderer;
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.ComponentList;
import org.riotfamily.components.model.ComponentListLocation;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.support.RequestContextUtils;

public class InheritingRenderStrategy extends AbstractRenderStrategy {

	private MessageSource messageSource;

	public InheritingRenderStrategy(ComponentDao dao,
			ComponentRepository repository) {

		super(dao, repository);
		this.messageSource = repository.getMessageSource();
	}

	/**
	 * Return the preview components in case the list is marked as dirty,
	 * the live components otherwise.
	 */
	protected List getComponentsToRender(ComponentList list) {
		if (list.isDirty()) {
			return list.getPreviewComponents();
		}
		return list.getLiveComponents();
	}

	protected void renderComponent(ComponentRenderer renderer,
			Component component, int position, int listSize,
			ComponentListConfiguration config, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		renderer.render(component, true, position, listSize, request, response);
	}

	protected void renderComponentList(ComponentList list,
			ComponentListConfiguration config,
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {
		
		boolean live = EditModeUtils.isLiveMode(request);
		EditModeUtils.setLiveMode(request, true);
		super.renderComponentList(list, config, request, response);
		EditModeUtils.setLiveMode(request, live);
	}

	protected void onListNotFound(ComponentListLocation location,
			ComponentListConfiguration config, 
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {
		
		Locale locale = RequestContextUtils.getLocale(request);
		TagWriter tag = new TagWriter(response.getWriter());
		tag.start(Html.DIV)
				.attribute(Html.COMMON_CLASS, "riot-no-inheritance")
				.body(messageSource.getMessage(
				"components.inheritance.noParentList", null,
				"No parent list available", locale))
				.end();
	}

	protected void onEmptyComponentList(ComponentListConfiguration config, 
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {
		
		Locale locale = RequestContextUtils.getLocale(request);
		TagWriter tag = new TagWriter(response.getWriter());
		tag.start(Html.DIV)
				.attribute(Html.COMMON_CLASS, "riot-no-inheritance")
				.body(messageSource.getMessage(
				"components.inheritance.emptyParentList", null,
				"The parent list does not contain any components", locale))
				.end();
	}

}
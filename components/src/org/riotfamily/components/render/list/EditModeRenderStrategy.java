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
package org.riotfamily.components.render.list;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.markup.DocumentWriter;
import org.riotfamily.common.markup.Html;
import org.riotfamily.components.config.ComponentListConfig;
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.ComponentList;
import org.riotfamily.components.render.component.ComponentRenderer;
import org.riotfamily.components.render.component.EditModeComponentDecorator;
import org.riotfamily.forms.factory.FormRepository;
import org.springframework.util.StringUtils;

public class EditModeRenderStrategy extends DefaultRenderStrategy {

	private EditModeComponentDecorator editModeRenderer;
	
	public EditModeRenderStrategy(ComponentDao dao, ComponentRenderer renderer,
			FormRepository formRepository, ComponentListRenderer listRenderer) {
		
		super(dao, renderer);
		editModeRenderer = new EditModeComponentDecorator(renderer, formRepository);
		listRenderer.setEditModeRenderStrategy(this);
	}

	/**
	 * Overrides the default implementation to render a DIV tag around the
	 * actual list. The DIV has attributes that are required for the
	 * Riot toolbar JavaScript.
	 */
	public void render(ComponentList list, 
			ComponentListConfig config,
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {
		
		DocumentWriter wrapper = new DocumentWriter(response.getWriter());
		
		wrapper.start(Html.DIV)
			.attribute(Html.COMMON_CLASS, "riot-component-list")
			.attribute("riot:listId", list.getId().toString());
		
		if (config.getValidComponentTypes() != null) {
			wrapper.attribute("riot:validTypes", 
					StringUtils.collectionToCommaDelimitedString(
					config.getValidComponentTypes()));
		}
		if (config.getMinComponents() != null) {
			wrapper.attribute("riot:minComponents", 
					config.getMinComponents().intValue());
		}
		if (config.getMaxComponents() != null) {
			wrapper.attribute("riot:maxComponents", 
					config.getMaxComponents().intValue());
		}
		
		wrapper.body();
		super.render(list, config, request, response);
		wrapper.closeAll();
	}

	/**
	 * Overrides the default implementation to render a DIV tag around the
	 * actual component. The DIV has attributes that are required for the
	 * Riot toolbar JavaScript.
	 * @throws IOException
	 */
	protected void renderComponent(Component component, 
			int position, int listSize, ComponentListConfig config, 
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {

		editModeRenderer.render(component, position, listSize, request, response);
	}

}

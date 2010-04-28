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
package org.riotfamily.components.render.list;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.components.config.ComponentListConfig;
import org.riotfamily.components.meta.ComponentMetaDataProvider;
import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.ComponentList;
import org.riotfamily.components.render.component.ComponentRenderer;
import org.riotfamily.components.render.component.EditModeComponentRenderer;
import org.riotfamily.forms.Form;

public class EditModeRenderStrategy extends DefaultRenderStrategy {

	private EditModeComponentRenderer editModeRenderer;
	
	public EditModeRenderStrategy(ComponentRenderer renderer,
			ComponentMetaDataProvider metaDataProvider,
			Map<String, Form> forms, ComponentListRenderer listRenderer) {
		
		super(renderer);
		editModeRenderer = new EditModeComponentRenderer(
				renderer, metaDataProvider, forms);
		
		listRenderer.setEditModeRenderStrategy(this);
	}
	
	/**
	 * Overrides the default implementation to render a DIV tag around the
	 * actual list. The DIV has attributes that are required for the
	 * Riot toolbar JavaScript.
	 */
	@Override
	public void render(ComponentList list, ComponentListConfig config,
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {
		
		PrintWriter out = response.getWriter();
		out.printf("<div class=\"riot-component-list\" riot:listId=\"%s\">", list.getCompositeId());
		super.render(list, config, request, response);
		out.print("</div>");
		out.printf("<script>riotComponentListConfig%s = %s;</script>", list.getCompositeId(), config.toJSON());
	}

	/**
	 * Overrides the default implementation to render a DIV tag around the
	 * actual component. The DIV has attributes that are required for the
	 * Riot toolbar JavaScript.
	 * @throws IOException
	 */
	@Override
	protected void renderComponent(Component component, 
			ComponentListConfig config, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		editModeRenderer.render(component, request, response);
	}

}

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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.DocumentWriter;
import org.riotfamily.components.config.ComponentListConfig;
import org.riotfamily.components.meta.ComponentMetaDataProvider;
import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.ComponentList;
import org.riotfamily.components.render.component.ComponentRenderer;
import org.riotfamily.components.render.component.EditModeComponentRenderer;
import org.riotfamily.forms.factory.FormRepository;

public class EditModeRenderStrategy extends DefaultRenderStrategy {

	private EditModeComponentRenderer editModeRenderer;
	
	public EditModeRenderStrategy(ComponentRenderer renderer,
			ComponentMetaDataProvider metaDataProvider,
			FormRepository formRepository, ComponentListRenderer listRenderer) {
		
		super(renderer);
		editModeRenderer = new EditModeComponentRenderer(
				renderer, metaDataProvider, formRepository);
		
		listRenderer.setEditModeRenderStrategy(this);
	}
	
	/**
	 * Overrides the default implementation to render a DIV tag around the
	 * actual list. The DIV has attributes that are required for the
	 * Riot toolbar JavaScript.
	 */
	@Override
	public void render(ComponentList list, 
			ComponentListConfig config,
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {
		
		DocumentWriter doc = new DocumentWriter(response.getWriter());
		
		doc.start("div")
			.attribute("class", "riot-component-list")
			.attribute("riot:listId", list.getCompositeId());
		
		doc.body();
		super.render(list, config, request, response);
		doc.end();
		
		doc.start("script").body("riotComponentListConfig" + list.getCompositeId() 
				+ " = " + config.toJSON() + ";", false);
		
		doc.end();
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

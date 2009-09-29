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
package org.riotfamily.components.render.component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.TagWriter;
import org.riotfamily.components.meta.ComponentMetaDataProvider;
import org.riotfamily.components.model.Component;
import org.riotfamily.forms.factory.FormRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EditModeComponentRenderer implements ComponentRenderer {

	private Logger log = LoggerFactory.getLogger(EditModeComponentRenderer.class);
	
	private FormRepository formRepository;
	
	private ComponentRenderer renderer;
	
	private ComponentMetaDataProvider metaDataProvider;
	
	public EditModeComponentRenderer(ComponentRenderer renderer, 
			ComponentMetaDataProvider metaDataProvider,
			FormRepository formRepository) {
		
		this.renderer = renderer;
		this.metaDataProvider = metaDataProvider;
		this.formRepository = formRepository;
	}

	public void render(Component component, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String type = component.getType();
		
		String className = "riot-content riot-component " +
				"riot-component-" + type;
		
		String formId = metaDataProvider.getMetaData(type).getForm();
		if (formId != null) {
			if (!formRepository.containsForm(formId)) {
				log.error("The configured component form [%s] does not exist", formId);
				formId = null;
			}
		}
		else if (formRepository.containsForm(type)) {
			formId = type;
		}
		
		if (formId != null) {
			className += " riot-form";
		}
		
		TagWriter wrapper = new TagWriter(response.getWriter());
		wrapper.start("div")
				.attribute("class", className)
				.attribute("riot:contentId", component.getCompositeId())
				.attribute("riot:componentType", type)
				.attribute("riot:form", formId)
				.body();

		renderer.render(component, request, response);
		
		wrapper.end();
	}
}

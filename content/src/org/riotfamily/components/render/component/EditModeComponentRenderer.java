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

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.components.meta.ComponentMetaDataProvider;
import org.riotfamily.components.model.Component;
import org.riotfamily.forms2.Form;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EditModeComponentRenderer implements ComponentRenderer {

	private Logger log = LoggerFactory.getLogger(EditModeComponentRenderer.class);
	
	private Map<String, Form> forms;
	
	private ComponentRenderer renderer;
	
	private ComponentMetaDataProvider metaDataProvider;
	
	public EditModeComponentRenderer(ComponentRenderer renderer, 
			ComponentMetaDataProvider metaDataProvider,
			Map<String, Form> forms) {
		
		this.renderer = renderer;
		this.metaDataProvider = metaDataProvider;
		this.forms = forms;
	}

	public void render(Component component, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String type = component.getType();
		
		String className = "riot-content riot-component " +
				"riot-component-" + type;
		
		String formId = metaDataProvider.getMetaData(type).getForm();
		if (formId != null) {
			if (!forms.containsKey(formId)) {
				log.error("The configured component form [{}] does not exist", formId);
				formId = null;
			}
		}
		else if (forms.containsKey(type)) {
			formId = type;
		}
		
		if (formId != null) {
			className += " riot-form";
		}
		
		PrintWriter out = response.getWriter();
		out.printf("<div class=\"%s\" riot:contentId=\"%s\" riot:componentType=\"%s\" riot:form=\"%s\">", 
				className, component.getCompositeId(), type, formId);

		renderer.render(component, request, response);
		
		out.print("</div>");
	}
}

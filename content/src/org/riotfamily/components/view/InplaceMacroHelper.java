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
package org.riotfamily.components.view;

import java.io.StringWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.support.CapturingResponseWrapper;
import org.riotfamily.components.config.ComponentListConfig;
import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.ContentMap;
import org.riotfamily.components.render.component.ComponentRenderer;
import org.riotfamily.components.render.list.ComponentListRenderer;
import org.riotfamily.components.support.EditModeUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class InplaceMacroHelper {

	private HttpServletRequest request;
	
	private HttpServletResponse response;

	private List<String> toolbarScripts;

	private List<DynamicToolbarScript> dynamicToolbarScripts;
	
	private ComponentListRenderer componentListRenderer;
	
	private ComponentRenderer componentRenderer;
	
	public InplaceMacroHelper(HttpServletRequest request,
			HttpServletResponse response, 
			List<String> toolbarScripts,
			List<DynamicToolbarScript> dynamicToolbarScripts, 
			ComponentListRenderer componentListRenderer,
			ComponentRenderer componentRenderer) {

		this.request = request;
		this.response = response;
		this.toolbarScripts = toolbarScripts;
		this.dynamicToolbarScripts = dynamicToolbarScripts;
		this.componentListRenderer = componentListRenderer;
		this.componentRenderer = componentRenderer;
	}

	public boolean isEditMode() {
		return EditModeUtils.isEditMode(request);
	}
	
	public boolean isPreviewMode() {
		return EditModeUtils.isPreviewMode(request);
	}
	
	public boolean isLiveMode() {
		return EditModeUtils.isLiveMode(request);
	}
	
	public boolean isEditable(ContentMap contentMap) {
		return EditModeUtils.isEditable("edit", 
				contentMap.getContent().getContainer().getOwner(), request);
	}
	
	public List<String> getToolbarScripts() {
		return this.toolbarScripts;
	}
	
	public String getInitScript() {
		StringBuffer sb = new StringBuffer();
		for (DynamicToolbarScript script : dynamicToolbarScripts) {
			String js = script.generateJavaScript(request);
			if (js != null) {
				sb.append(js).append('\n');
			}
		}
		return sb.toString();
	}
		
	public String renderComponents(ContentMap contentMap, 
			String key, Integer minComponents, Integer maxComponents,
			List<String> initalComponentTypes, 
			List<?> validComponentTypes, int x, int y)
			throws Exception {
		
		ComponentListConfig config = new ComponentListConfig(minComponents, 
				maxComponents, initalComponentTypes, validComponentTypes, x, y);
		
		return componentListRenderer.renderComponents(contentMap, key, config, 
				request, response);
	}
	
	public String renderComponent(Component component) throws Exception {
		StringWriter sw = new StringWriter();
		if (component != null) {
			request.setAttribute("readOnlyComponent", true);
			componentRenderer.render(component, request, new CapturingResponseWrapper(response, sw));
			request.removeAttribute("readOnlyComponent");
		}
		return sw.toString();
	}
	
}

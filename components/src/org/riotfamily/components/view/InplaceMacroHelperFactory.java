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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.view.MacroHelperFactory;
import org.riotfamily.components.render.list.ComponentListRenderer;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class InplaceMacroHelperFactory implements MacroHelperFactory {

	private List<String> toolbarScripts = Collections.emptyList();
	
	private List<DynamicToolbarScript> dynamicToolbarScripts = Collections.emptyList();

	private ComponentListRenderer componentListRenderer;

	public InplaceMacroHelperFactory(
			ComponentListRenderer componentListRenderer) {
		
		this.componentListRenderer = componentListRenderer;
	}
	
	public void setToolbarScripts(List<String> toolbarScripts) {
		this.toolbarScripts = toolbarScripts;
	}
	
	public void setDynamicToolbarScripts(List<DynamicToolbarScript> dynamicToolbarScripts) {
		this.dynamicToolbarScripts = dynamicToolbarScripts;
	}

	public Object createMacroHelper(HttpServletRequest request,
			HttpServletResponse response, Map<String, ?> model) {

		
		return new InplaceMacroHelper(request, response, toolbarScripts, 
				dynamicToolbarScripts, componentListRenderer);
	}
}

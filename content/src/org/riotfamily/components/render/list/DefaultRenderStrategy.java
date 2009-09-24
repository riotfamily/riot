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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.RiotLog;
import org.riotfamily.components.config.ComponentListConfig;
import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.ComponentList;
import org.riotfamily.components.render.component.ComponentRenderer;

public class DefaultRenderStrategy implements RenderStrategy {
	
	protected RiotLog log = RiotLog.get(getClass());
	
	private ComponentRenderer renderer;
	
	public DefaultRenderStrategy(ComponentRenderer renderer) {
		this.renderer = renderer;
	}
		
	public void render(ComponentList list,
			ComponentListConfig config,
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {
		
		if (list == null || list.isEmpty()) {
			onEmptyComponentList(config, request, response);
			return;
		}
		for (Component component : list) {
			renderComponent(component, config, request, response);
		}
	}
	
	protected void onEmptyComponentList(ComponentListConfig config, 
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {

	}
	
	protected void renderComponent(Component component, 
			ComponentListConfig config, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		renderer.render(component, request, response);
	}

}

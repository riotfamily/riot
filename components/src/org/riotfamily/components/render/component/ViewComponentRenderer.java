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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.view.ViewResolutionException;
import org.riotfamily.common.view.ViewResolverHelper;
import org.riotfamily.components.model.Component;
import org.springframework.web.servlet.View;

/**
 * ComponentRenderer implementation that resolves a view-name just like 
 * Spring's DispatcherServlet and renders the view passing the 
 * Component's properties as model.
 */
public class ViewComponentRenderer extends AbstractComponentRenderer {

	private String viewNamePrefix = "";
	
	private String viewNameSuffix = "";
	
	private ViewResolverHelper viewResolverHelper;
	
	public void setViewNamePrefix(String viewNamePrefix) {
		this.viewNamePrefix = viewNamePrefix;
	}

	public void setViewNameSuffix(String viewNameSuffix) {
		this.viewNameSuffix = viewNameSuffix;
	}

	public void setViewResolverHelper(ViewResolverHelper viewResolverHelper) {
		this.viewResolverHelper = viewResolverHelper;
	}
	
	public View getView(String type) {
		try {
			String viewName = viewNamePrefix + type + viewNameSuffix;
			return viewResolverHelper.resolveView(Locale.getDefault(), viewName);
		}
		catch (ViewResolutionException e) {
			return null;
		}
	}
	
	@Override
	protected void renderInternal(Component component, 
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {

		Map<String, Object> model = new HashMap<String, Object>();
		model.putAll(component);
		
		model.put("contentMap", component);
		model.put(THIS, component);
		model.put(POSITION, component.getPosition());
		model.put(LIST_SIZE, component.getList().size());
		
		try {
			String viewName = viewNamePrefix + component.getType() + viewNameSuffix;
			View view = viewResolverHelper.resolveView(request, viewName);
			view.render(model, request, response);
		}
		catch (ViewResolutionException e) {
			log.warn("ViewResolutionException - Skipping component ...", e);
		}
	}	

}

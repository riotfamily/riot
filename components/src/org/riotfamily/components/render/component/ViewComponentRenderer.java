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
package org.riotfamily.components.render.component;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.view.ViewResolutionException;
import org.riotfamily.common.web.view.ViewResolverHelper;
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
	
	protected void renderInternal(Component component, 
			int position, int listSize, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Map<String, Object> model = new HashMap<String, Object>();
		Map<String, Object> props = component.unwrap();
		if (props != null) {
			model.putAll(props);
		}
		
		model.put(THIS, component);
		model.put(POSITION, new Integer(position));
		model.put(LIST_SIZE, new Integer(listSize));
		model.put(PARENT, request.getAttribute(PARENT_ATTRIBUTE));
		
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

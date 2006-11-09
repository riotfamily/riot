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
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.component.impl;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.spring.ViewResolverHelper;
import org.riotfamily.pages.component.ComponentVersion;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

/**
 * Component implementation that resolves a view-name just like Spring's
 * DispatcherServlet and renders the view passing the ComponentVersion's 
 * properties as model. 
 */
public class ViewComponent extends AbstractComponent 
		implements ApplicationContextAware {
	
	private ViewResolverHelper viewResolverHelper;

	private String viewName;
	
	private boolean dynamic = false;
	
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		viewResolverHelper = new ViewResolverHelper(applicationContext);
	}
	
	protected void renderInternal(ComponentVersion componentVersion, 
			String positionClassName, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		Map model = buildModel(componentVersion);
		model.put(COMPONENT_ID, String.valueOf(componentVersion.getId()));
		model.put(POSITION_CLASS, positionClassName);
		ModelAndView mv = new ModelAndView(viewName, model);
		View view = viewResolverHelper.resolveView(request, mv);
		view.render(model, request, response);
	}
	
	public boolean isDynamic() {
		return this.dynamic;
	}
	
	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}
	
}

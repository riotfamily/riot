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
package org.riotfamily.components.config.component;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.view.ViewResolutionException;
import org.riotfamily.common.web.view.ViewResolverHelper;
import org.riotfamily.components.model.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Component implementation that resolves a view-name just like Spring's
 * DispatcherServlet and renders the view passing the ComponentVersion's
 * properties as model.
 */
public class ViewComponent extends AbstractComponent {

	private String viewName;

	private boolean dynamic = false;

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	protected void renderInternal(Component component, boolean preview,
			int position, int listSize, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Map model = new HashMap();
		Map props = component.unwrapValues(preview);
		if (props != null) {
			model.putAll(props);
		}
		
		model.put(THIS, component);
		model.put(POSITION, new Integer(position));
		model.put(LIST_SIZE, new Integer(listSize));

		Component parentComponent = component.getList().getParent();
		if (parentComponent != null) {
			request.setAttribute(PARENT, parentComponent);
		}

		ModelAndView mv = new ModelAndView(viewName, model);
		try {
			View view = new ViewResolverHelper(
					RequestContextUtils.getWebApplicationContext(request))
					.resolveView(request, mv);

			view.render(model, request, response);
		}
		catch (ViewResolutionException e) {
			log.warn("ViewResolutionException - Skipping component ...", e);
		}
	}

	public boolean isDynamic() {
		return this.dynamic;
	}

	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}

}

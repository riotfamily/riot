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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.components;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.spring.AbstractCacheableController;
import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.common.web.view.ViewResolverHelper;
import org.riotfamily.components.context.PageRequestUtils;
import org.riotfamily.components.editor.EditModeUtils;
import org.riotfamily.riot.security.AccessController;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class ComponentSetController extends AbstractCacheableController
		implements ApplicationContextAware {

	public static final String MODEL_KEY = ComponentSetController.class.getName();

	private static final Map MODEL = Collections.singletonMap(
			MODEL_KEY, Boolean.TRUE);

	protected String viewName;

	protected ViewResolverHelper viewResolverHelper;

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.viewResolverHelper = new ViewResolverHelper(applicationContext);
	}

	public long getTimeToLive() {
		return CACHE_ETERNALLY;
	}

	protected boolean bypassCache(HttpServletRequest request) {
		return AccessController.isAuthenticatedUser();
	}

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		if (EditModeUtils.isEditMode(request)) {
			String uri = ServletUtils.getPathWithinApplication(request);
			if (PageRequestUtils.createAndStoreContext(request, uri, 120000)) {
				View view = viewResolverHelper.resolveView(request, viewName);
				return new ModelAndView(new ComponentSetView(view, uri), MODEL);
			}
		}
		return new ModelAndView(viewName, MODEL);
	}

	private static class ComponentSetView implements View {

		private View view;

		private String uri;

		public ComponentSetView(View view, String uri) {
			this.view = view;
			this.uri = uri;
		}

		public String getContentType() {
			return view.getContentType();
		}

		public void render(Map model, HttpServletRequest request,
				HttpServletResponse response) throws Exception {

			PrintWriter out = response.getWriter();
			out.print("<div class=\"riot-components\" riot:controllerId=\"");
			out.print(uri);
			out.print("\">");
			view.render(model, request, response);
			out.print("</div>");
		}
	}

}

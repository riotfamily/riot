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
 *   Jan-Frederic Linde [jfl at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.editor.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.common.web.util.ServletUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.RedirectView;

public class FrameSetController implements Controller {
	
	public static final String REQUESTED_URL_PARAM = "url";
	
	private String viewName = ResourceUtils.getPath(
			FrameSetController.class, "FrameSetView.ftl");
			
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}		
	
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ModelAndView mv = null;
		HttpSession session = request.getSession();
		if (request.getParameter(REQUESTED_URL_PARAM) != null) {
			session.setAttribute(REQUESTED_URL_PARAM, request.getParameter(REQUESTED_URL_PARAM));
			mv = new ModelAndView(new RedirectView(ServletUtils.getOriginatingRequestUri(request)));
		}
		else {
			mv = new ModelAndView(viewName);
			if (session.getAttribute(REQUESTED_URL_PARAM) != null) {
				mv.addObject(REQUESTED_URL_PARAM, request.getSession().getAttribute(REQUESTED_URL_PARAM));
				session.removeAttribute(REQUESTED_URL_PARAM);
			}
		}
		return mv;
	}
	
}

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
package org.riotfamily.core.screen;

import java.io.StringWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.servlet.DummyHttpServletResponse;
import org.riotfamily.common.util.Generics;
import org.riotfamily.common.view.ViewResolverHelper;
import org.riotfamily.forms.TemplateUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

public class ModelAndViewScreenlet implements Screenlet, ApplicationContextAware {

	private ViewResolverHelper viewResolverHelper;
	
	public void setApplicationContext(ApplicationContext context) {
        viewResolverHelper = new ViewResolverHelper(context);
    }
	
	public String render(ScreenContext context) throws Exception {
		StringWriter sw = new StringWriter();
		HttpServletResponse response = new DummyHttpServletResponse(sw);
		ModelAndView mv = handleRequest(context);
		HttpServletRequest request = context.getRequest();
		View view = viewResolverHelper.resolveView(request, mv);
		view.render(mv.getModel(), request, response);
		return sw.toString();
	}

	protected String getViewName() {
		return TemplateUtils.getTemplatePath(getTemplateClass());
	}
	
	protected Class<?> getTemplateClass() {
		return getClass();
	}

	protected ModelAndView handleRequest(ScreenContext context) {
		Map<String, Object> model = Generics.newHashMap();
		populateModel(model, context);
		ModelAndView mv = new ModelAndView(getViewName(), model);
		return mv;
	}

	protected void populateModel(Map<String, Object> model, 
			ScreenContext context) {
	}

}

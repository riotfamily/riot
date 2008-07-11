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
 *   Carsten Woelk [cwoelk at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.SpringUtils;
import org.riotfamily.pages.mapping.PageResolver;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.view.PageToViewNameTranslator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0
 */
public class PageController implements Controller, ApplicationContextAware {

	private PageToViewNameTranslator pageToViewNameTranslator;
	
	private ApplicationContext applicationContext;
	
	public final void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
		pageToViewNameTranslator = SpringUtils.beanOfType(applicationContext, 
				PageToViewNameTranslator.class);
	}
	
	protected final ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public final ModelAndView handleRequest(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		Page page = PageResolver.getResolvedPage(request);
		
		ExtendedModelMap model = new ExtendedModelMap();
		populateModel(model, page, request, response);
		
		String viewName = pageToViewNameTranslator.getViewName(page);
		return new ModelAndView(viewName, model);
	}
	
	protected void populateModel(Model model, Page page,
			HttpServletRequest request, HttpServletResponse response) {
		
		populateModel(model, page);
	}

	protected void populateModel(Model model, Page page) {
	}
	
}

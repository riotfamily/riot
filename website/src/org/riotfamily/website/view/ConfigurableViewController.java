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
package org.riotfamily.website.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
* Class similar to Spring's 
* {@link org.springframework.web.servlet.mvc.ParameterizableViewController}, 
* with the following two differences:
* <ul>
*   <li>
*     Setting a viewName is not required. If omitted Spring's 
*     {@link org.springframework.web.servlet.RequestToViewNameTranslator} 
*     will be used by the DispatcherServlet.
*   </li>
*   <li>
*     A Content-Type may be set.
*   </li>
* </ul> 
* 
* @author Carsten Woelk [cwoelk at neteye dot de]
* @author Felix Gnass [fgnass at neteye dot de]
* @since 8.0
*/
public class ConfigurableViewController implements Controller {

	private String viewName;

	private String contentType;
	
	/**
	 * Sets the name of the view to delegate to.
	 */
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	/**
	 * Sets the content type to be set in the header.
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	/**
	 * Creates a {@link ModelAndView} with the configured viewName and invokes
	 * {@link #populateModel(Model, HttpServletRequest)}.
	 * If a contentType is configured, 
	 * {@link HttpServletResponse#setContentType(String)} is invoked.
	 */
	public ModelAndView handleRequest(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {

		if (contentType != null) {
			response.setContentType(contentType);
		}
		
		ExtendedModelMap model = new ExtendedModelMap();
		ModelAndView mv = new ModelAndView(viewName, model);
		populateModel(model, request);
		return mv;
	}
	
	/**
	 * Can be overwritten by subclasses to populate the model. The default
	 * implementation does nothing, hence an empty model is passed to the view.
	 */
	protected void populateModel(Model model, HttpServletRequest request) {
	}
	
}

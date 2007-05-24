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
package org.riotfamily.website.generic;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator;

/**
 * <p>Trivial controller that allows you to set a content type for the view using
 * an exposed configuration property. A view name also is not required to be set
 * as if ommited the configured viewNameTranslator will translate the view. Per
 * default Spring is using its {@link DefaultRequestToViewNameTranslator} for
 * this purpose.</p>
 * 
 * <p>If you are using FreeMarker as your View technology you might want to configure
 * the viewNameTranslator with the suffix '.ftl' to work properly:
 * <code> 
 * <bean id="viewNameTranslator" class="org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator">
 *   <property name="suffix" value=".ftl" />
 * </bean>
 * </code>
 * </p>
 *
 * @author Carsten Woelk [cwoelk at neteye dot de]
 * @since 6.5
 */
public class GenericViewController extends AbstractController {

	private String viewName;

	private String contentType;


	/**
	 * Set the name of the view to delegate to
	 * @param viewName the name of the view to delegate to
	 */
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	/**
	 * Returns the name of the view to delegate to
	 * @return the name of the view to delegate to
	 */
	public String getViewName() {
		return this.viewName;
	}

	/**
	 * Returns the content type to be set in the header
	 * @return the content type to be set in the header
	 */
	public String getContentType() {
		return this.contentType;
	}

	/**
	 * Sets the content type to be set in the header
	 * @param contentType to be set in the header
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}


	/**
	 * If configured sets the content on the request and returns a {@link ModelAndView}
	 * object with the specified view name if configured. If none has been set
	 * Spring will use its configure viewNameTranslator to resolve the view name.
	 * @see #getViewName()
	 */
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		if (contentType != null) {
			response.setContentType(contentType);
		}

		return new ModelAndView(getViewName());
	}



}

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
package org.riotfamily.common.web.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.view.JsonView;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

/**
 * Extends the {@link SimpleMappingExceptionResolver} and returns the {@link AjaxResponse}.
 */
public class XmlHttpRequestExceptionHandler 
		extends SimpleMappingExceptionResolver {

	private String exceptionMessageAttribute = "exceptionMessage";
	private String exceptionClassNameAttribute = "exceptionClassName";
	
	/**
	 * @param exceptionMessageAttribute the exceptionMessageAttribute to set
	 */
	public void setExceptionMessageAttribute(String exceptionMessageAttribute) {
		this.exceptionMessageAttribute = exceptionMessageAttribute;
	}

	/**
	 * @param exceptionClassNameAttribute the exceptionClassNameAttribute to set
	 */
	public void setExceptionClassNameAttribute(String exceptionClassNameAttribute) {
		this.exceptionClassNameAttribute = exceptionClassNameAttribute;
	}

	/**
	 * We need to return {@link AjaxResponse} to this type of request, otherwise SyntaxError exception
	 * will be raised on the client side.
	 */
	protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response,
				Object handler, Exception ex) {
		
		logger.error("Unhandled exception", ex);
		// Log exception, both at debug log level and at warn level, if desired.
		if (logger.isDebugEnabled()) {
			logger.debug("Resolving exception from handler [" + handler + "]: " + ex);
		}
		logException(ex, request);

		// Apply HTTP status code for error views, if specified.
		// Only apply it if we're processing a top-level request.
		Integer statusCode = determineStatusCode(request, null);
		if (statusCode != null) {
			applyStatusCodeIfPossible(request, response, statusCode.intValue());
		}
		
		ModelMap model = new ModelMap();
		model.put ( exceptionClassNameAttribute, ex.getClass().getName() );
		model.put ( exceptionMessageAttribute, ex.getMessage() );
		
		//The JsonView sets the contentType to application/json 
		//so that the model can be accessed via Ajax.Response#responseJSON in the JavaScript
		JsonView jsonView = new JsonView();
		
		return new ModelAndView(jsonView, model);
	}

	/**
	 * Applies to <code>XMLHttpRequest</code> coming from prototype.js
	 */
	protected boolean shouldApplyTo(HttpServletRequest request, Object handler) {
		return ServletUtils.isXmlHttpRequest(request);
	}
	
}
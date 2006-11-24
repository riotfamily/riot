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
package org.riotfamily.forms.controller;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.forms.Form;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class NamedMethodHandler implements FormSubmissionHandler {
	
	private static final Class[] HANDLER_PARAM_TYPES = new Class[] {
		Form.class, HttpServletRequest.class, HttpServletResponse.class
	};
	
	private Controller controller;
	
	private Method handlerMethod;
	
	public NamedMethodHandler(Controller controller, String methodName) {
		this.controller = controller;
		try {
			handlerMethod = controller.getClass().getMethod(
					methodName, HANDLER_PARAM_TYPES);
		}
		catch (SecurityException e) {
			throw new RuntimeException(e);
		}
		catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
		
		Assert.isTrue(ModelAndView.class.isAssignableFrom(
				handlerMethod.getReturnType()), 
				"Handler method must return a ModelAndView");
	}

	public ModelAndView handleFormSubmission(Form form, 
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		Object[] args = new Object[] {form, request, response};
		return (ModelAndView) handlerMethod.invoke(controller, args);
	}

}

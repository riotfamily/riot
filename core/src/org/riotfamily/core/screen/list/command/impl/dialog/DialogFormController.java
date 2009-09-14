/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.core.screen.list.command.impl.dialog;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.mapping.HandlerUrlUtils;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.controller.AjaxFormController;
import org.riotfamily.forms.controller.FormContextFactory;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class DialogFormController extends AjaxFormController {
	
	
	public DialogFormController(FormContextFactory formContextFactory) {
		super(formContextFactory);
	}
	
	/**
	 * Instead of creating a new form, this method looks up the form instance,
	 * that has been previously created by the DialogCommand.
	 */
	protected Form createForm(HttpServletRequest request) {
		Form form = getForm(request);
		if (form.getButtons().isEmpty()) {
			form.addButton("execute");
		}
		return form;
	}

	/**
	 * Returns the name of the session attribute under which the form is stored.
	 * This implementation expects that the controller is mapped via a handler
	 * mapping which exposes a path variable called 'formKey' that contains
	 * the name of the session attribute.
	 */
	protected String getSessionAttribute(HttpServletRequest request) {
		return HandlerUrlUtils.getPathVariable(request, "formKey");
	}
	
	/**
	 * Overwrites the super implementation to do nothing since the form
	 * created by the command is already populated by contract.
	 */
	protected void populateForm(Form form, HttpServletRequest request) {
	}
	
	public ModelAndView handleFormSubmission(Form form, 
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {

		return null;
	}	
	
}

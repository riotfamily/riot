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
package org.riotfamily.forms.controller;

import org.riotfamily.forms.Form;
import org.riotfamily.forms.event.Button;
import org.riotfamily.forms.event.ClickEvent;
import org.riotfamily.forms.event.ClickListener;
import org.springframework.web.servlet.mvc.Controller;

public class ButtonFactory {

	private String labelKey;
	
	private String label;
	
	private String cssClass;
	
	private FormSubmissionHandler formSubmissionHandler;
		
	
	public ButtonFactory(FormSubmissionHandler formSubmissionHandler) {
		this.formSubmissionHandler = formSubmissionHandler;
	}

	public ButtonFactory(Controller controller, String handlerMethodName) {
		this.formSubmissionHandler = new NamedMethodHandler(
				controller,	handlerMethodName);
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setLabelKey(String labelKey) {
		this.labelKey = labelKey;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	public Button createButton() {
		Button button = new Button();
		button.setSubmit(true);
		button.setTabIndex(1);
		if (label != null) {
			button.setLabel(label);
		}
		else {
			button.setLabelKey(labelKey);
		}
		if (cssClass != null) {
			button.setStyleClass(cssClass);
		}
		button.addClickListener(new ClickListener() {
			public void clicked(ClickEvent event) {
				Form form = event.getSource().getForm();
				form.setAttribute(AbstractFormController.FORM_SUBMISSION_HANDLER, 
						formSubmissionHandler);
			}
		});
		
		return button;
	}

}

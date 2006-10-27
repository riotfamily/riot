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
package org.riotfamily.forms.error;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.riotfamily.common.markup.DocumentWriter;
import org.riotfamily.common.markup.Html;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.bind.Editor;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;

public class FormErrors extends AbstractBindingResultSupport {	
	
	private static final String GENERAL_FORM_ERROR_MESSAGE_KEY = "error.form.hasErrors";
	
	private static final String GENERAL_FORM_ERROR_DEFAULT_MESSAGE = "Form has an error";
	
	private Form form;
	
	private String generalFormErrorMessageKey = GENERAL_FORM_ERROR_MESSAGE_KEY;
	
	private String generalFormErrorDefaultMessage = GENERAL_FORM_ERROR_DEFAULT_MESSAGE;

	public FormErrors(Form form) {
		super(form.getId());		
		this.form = form;
		setMessageCodesResolver(form.getFormContext().getMessageResolver()
				.getMessageCodesResolver());		
	}	
	
	public void setGeneralFormErrorDefaultMessage(
			String generalFormErrorDefaultMessage) {
		this.generalFormErrorDefaultMessage = generalFormErrorDefaultMessage;
	}
	
	public void setGeneralFormErrorMessageKey(String generalFormErrorMessageKey) {
		this.generalFormErrorMessageKey = generalFormErrorMessageKey;
	}

	public Object getTarget() {
		return form.getBackingObject();
	}	
	
	public void renderErrors(Element element) {		
		List errors = getErrors(element);
		if (errors != null) {
			PrintWriter writer = element.getForm().getFormContext().getWriter();
			DocumentWriter tag = new DocumentWriter(writer);
			tag.start(Html.UL)
					.attribute(Html.COMMON_ID, element.getId() + "-error")
					.attribute(Html.COMMON_CLASS, "errors");		
			Iterator it = errors.iterator();
			while (it.hasNext()) {
				tag.start(Html.LI)
						.body((String) it.next())
						.end();
			}			
			tag.end();	
		}
	}	
	
	public List getErrors(Element element) {
		if (element instanceof Editor) {
			ArrayList messages = new ArrayList();
			Editor editor = (Editor) element;
			List fieldErrors = getFieldErrors(editor.getFieldName());
			for (Iterator it = fieldErrors.iterator(); it.hasNext();) {
				FieldError error = (FieldError) it.next();
				String message = form.getFormContext().getMessageResolver().getMessage(error);
				if (!StringUtils.hasLength(message)) {
					message = StringUtils.arrayToCommaDelimitedString(error.getCodes());
				}
				messages.add(message);
			}
			return messages;
		}
		return null;
	}
	
	public void removeErrors(Element element) {
		if (element instanceof Editor) {			
			Editor editor = (Editor) element;
			removeErrors(getFieldErrors(editor.getFieldName()));			
		}		
	}
	
	public String getGeneralFormError() {
		return getGeneralFormError(null);
	}
	
	public String getGeneralFormError(Object[] args) {
		return form.getFormContext().getMessageResolver().getMessage(
					generalFormErrorMessageKey, args, 
					generalFormErrorDefaultMessage);
	}
	
	public boolean hasErrors(Element element) {
		if (!(element instanceof Editor)) {
			return false;
		}
		Editor editor = (Editor) element;
		return hasFieldErrors(editor.getFieldName());
	}
	
	public PropertyEditorRegistry getPropertyEditorRegistry() {
		return form.getEditorBinder();
	}
	
}

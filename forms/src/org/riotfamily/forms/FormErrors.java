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
package org.riotfamily.forms;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.riotfamily.common.markup.DocumentWriter;
import org.riotfamily.common.markup.Html;
import org.riotfamily.common.util.Generics;
import org.riotfamily.forms.support.AbstractBindingResultSupport;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;

public class FormErrors extends AbstractBindingResultSupport {

	private static final String GENERAL_FORM_ERROR_MESSAGE_KEY =
			"error.form.hasErrors";

	private static final String GENERAL_FORM_ERROR_DEFAULT_MESSAGE =
			"Please correct the error(s) below.";

	private Form form;

	public FormErrors(Form form) {
		super(form.getId());
		this.form = form;
		setMessageCodesResolver(form.getFormContext().getMessageResolver()
				.getMessageCodesResolver());
	}

	public Object getTarget() {
		return form.getBackingObject();
	}

	public Object getFieldValue(String field) {
		return form.getEditor(field).getValue();
	}

	public void renderErrors(Element element) {
		List<String> errors = getErrors(element);
		if (errors != null) {
			PrintWriter writer = element.getForm().getFormContext().getWriter();
			DocumentWriter tag = new DocumentWriter(writer);
			tag.start(Html.UL)
					.attribute(Html.COMMON_ID, element.getId() + "-error")
					.attribute(Html.COMMON_CLASS, "errors");
			Iterator<String> it = errors.iterator();
			while (it.hasNext()) {
				tag.start(Html.LI)
						.body(it.next())
						.end();
			}
			tag.end();
		}
	}

	public List<String> getErrors(Element element) {
		if (element instanceof Editor) {
			ArrayList<String> messages = Generics.newArrayList();
			Editor editor = (Editor) element;
			List<FieldError> fieldErrors = getFieldErrors(editor.getFieldName());
			for (Iterator<FieldError> it = fieldErrors.iterator(); it.hasNext();) {
				FieldError error = it.next();
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
		return form.getFormContext().getMessageResolver().getMessage(
				GENERAL_FORM_ERROR_MESSAGE_KEY, null,
				GENERAL_FORM_ERROR_DEFAULT_MESSAGE);
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

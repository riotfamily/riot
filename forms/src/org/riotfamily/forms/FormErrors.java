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
package org.riotfamily.forms;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.riotfamily.common.util.DocumentWriter;
import org.riotfamily.common.util.Generics;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.util.StringUtils;
import org.springframework.validation.AbstractErrors;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.ObjectError;

public class FormErrors extends AbstractErrors {

	private static final String GENERAL_FORM_ERROR_MESSAGE_KEY =
			"error.form.hasErrors";

	private static final String GENERAL_FORM_ERROR_DEFAULT_MESSAGE =
			"Please correct the error(s) below.";

	private Form form;
	
	private List<ObjectError> errors = Generics.newLinkedList();

	private MessageCodesResolver messageCodesResolver;
	
	public FormErrors(Form form) {
		this.form = form;
		this.messageCodesResolver = form.getFormContext().getMessageResolver()
				.getMessageCodesResolver();
	}
	
	public Form getForm() {
		return form;
	}

	/**
	 * Resolve the given error code into message codes.
	 * Calls the MessageCodesResolver with appropriate parameters.
	 * @param errorCode the error code to resolve into message codes
	 * @return the resolved message codes
	 * @see #setMessageCodesResolver
	 */
	private String[] resolveMessageCodes(String errorCode) {
		return messageCodesResolver.resolveMessageCodes(errorCode, getObjectName());
	}

	private String[] resolveMessageCodes(String errorCode, String field) {
		String fixedField = fixedField(field);
		Class<?> fieldType = getFieldType(fixedField);
		return messageCodesResolver.resolveMessageCodes(errorCode, 
				getObjectName(), fixedField, fieldType);
	}
	
	

	public void renderErrors(Element element) {
		List<String> errors = getErrors(element);
		if (errors != null) {
			PrintWriter writer = element.getForm().getFormContext().getWriter();
			DocumentWriter tag = new DocumentWriter(writer);
			tag.start("ul")
					.attribute("id", element.getId() + "-error")
					.attribute("class", "errors");
			
			for (String error : errors) {
				tag.start("li").body(error).end();
			}
			tag.end();
		}
	}

	public List<String> getErrors(Element element) {
		if (element instanceof Editor) {
			ArrayList<String> messages = Generics.newArrayList();
			Editor editor = (Editor) element;
			List<FieldError> fieldErrors = getFieldErrors(editor.getFieldName());
			for (FieldError error : fieldErrors) {
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
	
	public void removeError(ObjectError error) {
		errors.remove(error);
	}

	public void removeErrors(Element element) {
		if (element instanceof Editor) {
			Editor editor = (Editor) element;
			errors.removeAll(getFieldErrors(editor.getFieldName()));
		}
	}
	
	public void removeAllErrors() {
		errors.clear();
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

	public void addError(ObjectError error) {
		errors.add(error);
	}
	
	// -----------------------------------------------------------------------
	// Implementation of the Errors interface
	// -----------------------------------------------------------------------
	
	public Object getFieldValue(String field) {
		try {
			return form.getEditor(field).getValue();
		}
		catch (InvalidPropertyException e) {
			return null;
		}
	}
	
	public void addAllErrors(Errors errors) {
		this.errors.addAll(errors.getAllErrors());
		
	}

	public List<FieldError> getFieldErrors() {
		List<FieldError> result = Generics.newLinkedList();
		for (ObjectError error : errors) {
			if (error instanceof FieldError) {
				result.add((FieldError) error);
			}
		}
		return Collections.unmodifiableList(result);
	}

	public List<ObjectError> getGlobalErrors() {
		List<ObjectError> result = Generics.newLinkedList();
		for (ObjectError error : errors) {
			if (!(error instanceof FieldError)) {
				result.add(error);
			}
		}
		return Collections.unmodifiableList(result);
	}

	public String getObjectName() {
		return form.getId();
	}

	public void reject(String errorCode, Object[] args, String defaultMessage) {
		addError(new ObjectError(getObjectName(), 
				resolveMessageCodes(errorCode), args, defaultMessage));
	}

	public void rejectValue(String field, String errorCode, Object[] args, 
			String defaultMessage) {
		
		if ("".equals(getNestedPath()) && !StringUtils.hasLength(field)) {
			// We're at the top of the nested object hierarchy,
			// so the present level is not a field but rather the top object.
			// The best we can do is register a global error here...
			reject(errorCode, args, defaultMessage);
			return;
		}
		String fixedField = fixedField(field);
		Object newVal = getFieldValue(fixedField);
		FieldError fe = new FieldError(
				getObjectName(), fixedField, newVal, false,
				resolveMessageCodes(errorCode, field), args, defaultMessage);
		
		addError(fe);
	}
	
	

}

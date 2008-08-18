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
package org.riotfamily.forms.support;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.riotfamily.common.util.Generics;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.ObjectError;

public abstract class AbstractBindingResultSupport implements BindingResult {
	
	private final List<ObjectError> errors = Generics.newLinkedList();

	private final String objectName;

	private MessageCodesResolver messageCodesResolver;
	
	protected AbstractBindingResultSupport(String objectName) {
		this.objectName = objectName;
	}	

	public Map<String,Object> getModel() {
		Map<String,Object> model = Generics.newHashMap();
		// Errors instance, even if no errors.
		model.put(MODEL_KEY_PREFIX + getObjectName(), this);
		// Mapping from name to target object.
		model.put(getObjectName(), getTarget());
		return model;
	}

	public void recordSuppressedField(String fieldName) {		
	}

	public String[] getSuppressedFields() {		
		return null;
	}

	public void addError(ObjectError error) {
		this.errors.add(error);
	}
	
	public void removeError(ObjectError error) {
		this.errors.remove(error);
	}
	
	public void removeErrors(List<?> errors) {
		for (int i = 0; i < errors.size(); i++) {
			removeError((ObjectError) errors.get(i));
		}
	}
	
	public void removeAllErrors() {
		errors.clear();
	}
	
	public String[] resolveMessageCodes(String errorCode) {
		return getMessageCodesResolver().resolveMessageCodes(errorCode, getObjectName());
	}


	public String[] resolveMessageCodes(String errorCode, String field) {		
		return getMessageCodesResolver().resolveMessageCodes(errorCode, getObjectName(), field, null);
	}

	public String getObjectName() {
		return objectName;
	}
	
	public void setMessageCodesResolver(MessageCodesResolver messageCodesResolver) {
		this.messageCodesResolver = messageCodesResolver;
	}	

	public MessageCodesResolver getMessageCodesResolver() {
		return messageCodesResolver;
	}

	public void setNestedPath(String nestedPath) {				
	}

	public String getNestedPath() {		
		return null;
	}

	public void pushNestedPath(String subPath) {		
	}

	public void popNestedPath() throws IllegalStateException {		
	}

	public void reject(String errorCode) {
		reject(errorCode, null, null);
	}

	public void reject(String errorCode, String defaultMessage) {
		reject(errorCode, null, defaultMessage);
	}

	public void reject(String errorCode, Object[] errorArgs, String defaultMessage) {
		addError(new ObjectError(getObjectName(), resolveMessageCodes(errorCode), errorArgs, defaultMessage));
	}

	public void rejectValue(String field, String errorCode) {
		rejectValue(field, errorCode, null, null);
	}

	public void rejectValue(String field, String errorCode, String defaultMessage) {
		rejectValue(field, errorCode, null, defaultMessage);
	}

	public void rejectValue(String field, String errorCode, Object[] errorArgs, String defaultMessage) {		
		FieldError fe = new FieldError(
				getObjectName(), field, null, false,
				resolveMessageCodes(errorCode, field), errorArgs, defaultMessage);
		addError(fe);
	}

	@SuppressWarnings("unchecked")
	public void addAllErrors(Errors errors) {
		this.errors.addAll(errors.getAllErrors());
	}

	public boolean hasErrors() {
		return !this.errors.isEmpty();
	}
	
	public int getErrorCount() {
		return this.errors.size();
	}

	public List<ObjectError> getAllErrors() {
		return Collections.unmodifiableList(this.errors);
	}

	public boolean hasGlobalErrors() {
		return (getGlobalErrorCount() > 0);
	}

	public int getGlobalErrorCount() {
		return getGlobalErrors().size();
	}

	public List<ObjectError> getGlobalErrors() {
		List<ObjectError> result = Generics.newLinkedList();
		for (Iterator<ObjectError> it = this.errors.iterator(); it.hasNext();) {
			ObjectError error = it.next();
			if (!(error instanceof FieldError)) {
				result.add(error);
			}
		}
		return Collections.unmodifiableList(result);
	}
	
	public ObjectError getGlobalError() {
		for (Iterator<ObjectError> it = this.errors.iterator(); it.hasNext();) {
			ObjectError objectError = it.next();
			if (!(objectError instanceof FieldError)) {
				return objectError;
			}
		}
		return null;
	}

	public boolean hasFieldErrors() {
		return (getFieldErrorCount() > 0);
	}

	public int getFieldErrorCount() {
		return getFieldErrors().size();
	}

	public List<FieldError> getFieldErrors() {
		List<FieldError> result = Generics.newLinkedList();
		for (Iterator<ObjectError> it = this.errors.iterator(); it.hasNext();) {
			ObjectError error = it.next();
			if (error instanceof FieldError) {
				result.add((FieldError) error);
			}
		}
		return Collections.unmodifiableList(result);
	}

	public FieldError getFieldError() {
		for (Iterator<ObjectError> it = this.errors.iterator(); it.hasNext();) {
			Object error = it.next();
			if (error instanceof FieldError) {
				return (FieldError) error;
			}
		}
		return null;
	}

	public boolean hasFieldErrors(String field) {
		return (getFieldErrorCount(field) > 0);
	}

	public int getFieldErrorCount(String field) {
		return getFieldErrors(field).size();
	}

	public List<FieldError> getFieldErrors(String field) {
		List<FieldError> result = Generics.newLinkedList();
		
		for (Iterator<ObjectError> it = this.errors.iterator(); it.hasNext();) {
			ObjectError error = it.next();
			if (error instanceof FieldError && isMatchingFieldError(field, ((FieldError) error))) {
				result.add((FieldError) error);
			}
		}
		return Collections.unmodifiableList(result);
	}

	public FieldError getFieldError(String field) {
		for (Iterator<ObjectError> it = this.errors.iterator(); it.hasNext();) {
			Object error = it.next();
			if (error instanceof FieldError) {
				FieldError fe = (FieldError) error;
				if (isMatchingFieldError(field, fe)) {
					return fe;
				}
			}
		}
		return null;
	}

	public Object getFieldValue(String field) {
		throw new UnsupportedOperationException();
	}
	
	public Class<?> getFieldType(String field) {
		throw new UnsupportedOperationException();
	}
	
	protected boolean isMatchingFieldError(String field, FieldError fieldError) {
		return (field.equals(fieldError.getField()) ||
				(field.endsWith("*") && fieldError.getField().startsWith(field.substring(0, field.length() - 1))));
	}

}

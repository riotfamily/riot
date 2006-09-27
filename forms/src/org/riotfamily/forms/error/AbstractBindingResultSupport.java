package org.riotfamily.forms.error;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.ObjectError;

public abstract class AbstractBindingResultSupport implements BindingResult {
	
	private final List errors = new LinkedList();

	private final String objectName;

	private MessageCodesResolver messageCodesResolver;
	
	protected AbstractBindingResultSupport(String objectName) {
		this.objectName = objectName;
	}	

	public Map getModel() {
		Map model = new HashMap();
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
	
	public void removeErrors(List errors) {
		for (int i = 0; i < errors.size(); i++) {
			removeError((ObjectError) errors.get(i));
		}
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

	public void addAllErrors(Errors errors) {
		this.errors.addAll(errors.getAllErrors());
	}

	public boolean hasErrors() {
		return !this.errors.isEmpty();
	}
	
	public int getErrorCount() {
		return this.errors.size();
	}

	public List getAllErrors() {
		return Collections.unmodifiableList(this.errors);
	}

	public boolean hasGlobalErrors() {
		return (getGlobalErrorCount() > 0);
	}

	public int getGlobalErrorCount() {
		return getGlobalErrors().size();
	}

	public List getGlobalErrors() {
		List result = new LinkedList();
		for (Iterator it = this.errors.iterator(); it.hasNext();) {
			Object error = it.next();
			if (!(error instanceof FieldError)) {
				result.add(error);
			}
		}
		return Collections.unmodifiableList(result);
	}
	
	public ObjectError getGlobalError() {
		for (Iterator it = this.errors.iterator(); it.hasNext();) {
			ObjectError objectError = (ObjectError) it.next();
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

	public List getFieldErrors() {
		List result = new LinkedList();
		for (Iterator it = this.errors.iterator(); it.hasNext();) {
			Object error = it.next();
			if (error instanceof FieldError) {
				result.add(error);
			}
		}
		return Collections.unmodifiableList(result);
	}

	public FieldError getFieldError() {
		for (Iterator it = this.errors.iterator(); it.hasNext();) {
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

	public List getFieldErrors(String field) {
		List result = new LinkedList();
		
		for (Iterator it = this.errors.iterator(); it.hasNext();) {
			Object error = it.next();
			if (error instanceof FieldError && isMatchingFieldError(field, ((FieldError) error))) {
				result.add(error);
			}
		}
		return Collections.unmodifiableList(result);
	}

	public FieldError getFieldError(String field) {
		for (Iterator it = this.errors.iterator(); it.hasNext();) {
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
	
	public Class getFieldType(String field) {
		throw new UnsupportedOperationException();
	}
	
	protected boolean isMatchingFieldError(String field, FieldError fieldError) {
		return (field.equals(fieldError.getField()) ||
				(field.endsWith("*") && fieldError.getField().startsWith(field.substring(0, field.length() - 1))));
	}

}

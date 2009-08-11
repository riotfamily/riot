package org.riotfamily.website.form;

import java.util.Collection;
import java.util.List;

import org.riotfamily.common.util.RiotLog;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.support.RequestContext;

public class FormMacroHelper {

	private RiotLog log = RiotLog.get(FormMacroHelper.class);
	
	private String command;
	
	private RequestContext requestContext;

	public FormMacroHelper() {
	}

	public BindStatus getBindStatus(RequestContext requestContext, 
			String command, String field) {
		
		String path = command;
		if (StringUtils.hasText(field)) {
			path += '.' + field;
		}
		try {
			return requestContext.getBindStatus(path);
		}
		catch (IllegalStateException ex) {
			log.info(ex.getMessage());
		}
		return null;
	}
	
	public void start(String command, RequestContext requestContext) {
		this.command = command;
		this.requestContext = requestContext;
	}
	
	public void end() {
		this.command = null;
		this.requestContext = null;
	}
	
	public String getCommand() {
		return command;
	}
	
	private BindStatus getStatus(String path, RequestContext requestContext) {
		if (requestContext != null) {
			return requestContext.getBindStatus(path);
		}
		return null;
	}
	
	private BindStatus getFieldStatus(String field) {
		if (this.command != null) {
			return getStatus(this.command + '.' + field, this.requestContext);
		}
		return null;
	}

	private Errors getErrors() {
		if (this.command != null) {
			return getErrors(this.command, this.requestContext);
		}
		return null;
	}
	
	private Errors getErrors(String command, RequestContext requestContext) {
		BindStatus status = getStatus(command, requestContext);
		if (status != null) {
			return status.getErrors();
		}
		return null;
	}

	public boolean hasErrors(String command, RequestContext requestContext) {
		Errors errors = getErrors(command, requestContext);
		return errors != null && errors.hasErrors();
	}
	
	@SuppressWarnings("unchecked")
	public List<ObjectError> getAllErrors() {
		Errors errors = getErrors();
		if (errors != null) {
			return errors.getAllErrors();
		}
		return null;
	}

	public boolean hasGlobalErrors() {
		Errors errors = getErrors();
		return errors != null && errors.hasGlobalErrors();
	}

	@SuppressWarnings("unchecked")
	public List<ObjectError> getGlobalErrors() {
		Errors errors = getErrors();
		if (errors != null) {
			return errors.getGlobalErrors();
		}
		return null;
	}

	public boolean hasFieldErrors(String field) {
		List<FieldError> errors = getFieldErrors(field);
		return errors != null && !errors.isEmpty();
	}
	
	@SuppressWarnings("unchecked")
	public List<FieldError> getFieldErrors(String field) {
		Errors errors = getErrors();
		if (errors != null) {
			if (StringUtils.hasText(field)) {
				return errors.getFieldErrors(field);
			}
			else {
				return errors.getFieldErrors();
			}
		}
		return null;
	}

	public Object getValue(String field) {
		BindStatus status = getFieldStatus(field);
		if (status != null) {
			return status.getValue();
		}
		return null;
	}
	
	public String getDisplayValue(String field) {
		try {
			BindStatus status = getFieldStatus(field);
			if (status != null) {
				return status.getDisplayValue();
			}
		}
		catch (Exception e) {
			log.error("Error determing form field display value, %s.%s", e, command, field);
		}
		return "";
	}
	
	@SuppressWarnings("unchecked")
	public boolean isSelected(String field, String option) {
		Object value = getValue(field);
		if (value == null) {
			return false;
		}
		if (value instanceof Collection) {
			for (Object obj : (Collection) value) {
				if (obj != null && obj.toString().equals(option)) {
					return true;
				}
			}
			return false;
		}
		return value.toString().equals(option);
	}
	
}

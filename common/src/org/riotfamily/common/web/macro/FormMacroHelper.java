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
package org.riotfamily.common.web.macro;

import java.util.Collection;
import java.util.List;

import org.riotfamily.common.beans.property.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.support.RequestContext;

public class FormMacroHelper {

	private Logger log = LoggerFactory.getLogger(FormMacroHelper.class);
	
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
			return status.getActualValue();
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
			log.error("Error determing form field display value " 
					+ command + "." + field, e);
		}
		return "";
	}
	
	public boolean isSelected(String field, String option) {
		return isSelected(field, option, null);
	}
	
	@SuppressWarnings("rawtypes")
	public boolean isSelected(String field, String option, String valueProperty) {
		Object value = getValue(field);
		if (value == null) {
			return false;
		}
		if (value instanceof Collection) {
			for (Object obj : (Collection) value) {
				if (obj != null && isEqualValue(option, obj, valueProperty)) {
					return true;
				}
			}
			return false;
		}
		return isEqualValue(option, value, valueProperty);
	}
	
	private boolean isEqualValue(String option, Object value, String valueProperty) {
		if (StringUtils.hasText(valueProperty)) {
			try {
				return PropertyUtils.getPropertyAsString(value, valueProperty).equals(option);				
			} catch (InvalidPropertyException e) {
				//ignore
			}
		}
		return value.toString().equals(option);
	}
	
}

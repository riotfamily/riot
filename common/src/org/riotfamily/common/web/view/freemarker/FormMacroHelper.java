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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.web.view.freemarker;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.support.RequestContext;

public class FormMacroHelper {

	private static final Log log = LogFactory.getLog(FormMacroHelper.class);
	
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
	
	public boolean hasErrors(String command, RequestContext requestContext) {
		Errors errors = getErrors(command, requestContext);
		return errors != null && errors.hasErrors();
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
	
	public boolean hasFieldErrors(String field) {
		List errors = getFieldErrors(field);
		return errors != null && !errors.isEmpty();
	}
	
	public List getAllErrors() {
		Errors errors = getErrors();
		if (errors != null) {
			return errors.getAllErrors();
		}
		return null;
	}
	
	public List getFieldErrors(String field) {
		Errors errors = getErrors();
		if (errors != null) {
			return errors.getFieldErrors(field);
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
			log.error("Error determing form field display value, " + command + "." + field, e);
		}
		return "";
	}
	
	public boolean isSelected(String field, String option) {
		Object value = getValue(field);
		if (value == null) {
			return false;
		}
		if (value instanceof Collection) {
			Iterator it = ((Collection) value).iterator();
			while (it.hasNext()) {
				Object obj = it.next();
				if (obj != null && obj.toString().equals(option)) {
					return true;
				}
			}
			return false;
		}
		return value.toString().equals(option);
	}
	
}

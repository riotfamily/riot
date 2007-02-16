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
package org.riotfamily.common.beans.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.springframework.util.StringUtils;

public class BooleanEditor extends PropertyEditorSupport {

	public static final String VALUE_TRUE = "true";

	public static final String VALUE_FALSE = "false";

	private Boolean defaultValue;
	
	public BooleanEditor() {
		this(Boolean.FALSE);
	}
	
	public BooleanEditor(boolean defaultValue) {
		this(Boolean.valueOf(defaultValue));
	}
	
	public BooleanEditor(Boolean defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setDefaultValue(Boolean defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setAsText(String text) throws IllegalArgumentException {
		if (!StringUtils.hasText(text)) {
			setValue(defaultValue);
		}
		else if (VALUE_TRUE.equalsIgnoreCase(text)) {
			setValue(Boolean.TRUE);
		}
		else if (VALUE_FALSE.equalsIgnoreCase(text)) {
			setValue(Boolean.FALSE);
		}
		else {
			throw new IllegalArgumentException(
					"Invalid boolean value [" + text + "]");
		}
	}

	public String getAsText() {
		if (Boolean.TRUE.equals(getValue())) {
			return VALUE_TRUE;
		}
		else if (Boolean.FALSE.equals(getValue())) {
			return VALUE_FALSE;
		}
		else {
			return "";
		}
	}

}

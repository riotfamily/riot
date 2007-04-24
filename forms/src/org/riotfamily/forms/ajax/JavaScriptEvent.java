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
package org.riotfamily.forms.ajax;

import javax.servlet.http.HttpServletRequest;

/**
 * Serverside representation of a clientside JavaScript event.
 */
public class JavaScriptEvent {

	public static final int NONE = 0;
	
	public static final int ON_CLICK = 1;

	public static final int ON_CHANGE = 2;

	private int type;

	private String value;
	
	private String[] values;

	public JavaScriptEvent(HttpServletRequest request) {
		String submittedType = request.getParameter("event.type");
		if (submittedType.equals("click")) {
			this.type = ON_CLICK;
		}
		if (submittedType.equals("change")) {
			this.type = ON_CHANGE;
		}
		this.values = request.getParameterValues("source.value");
		this.value = request.getParameter("source.value");
	}

	public int getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	public String[] getValues() {
		return this.values;
	}

	public void setValues(String[] values) {
		this.values = values;
	}
	
}
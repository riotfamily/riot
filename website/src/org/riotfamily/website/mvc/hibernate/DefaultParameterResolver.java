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
package org.riotfamily.website.mvc.hibernate;

import javax.servlet.http.HttpServletRequest;

/**
 * ParameterResolver that first looks for a HTTP parameter with name returned
 * by <code>getName()</code>. If parameter is empty it returns null.
 * If no parameter is found, it looks for a request attribute with the name
 * returned by <code>getAttribute()</code>.
 */
public class DefaultParameterResolver extends AbstractParameterResolver {

	private String attribute;
	
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	
	public String getAttribute() {
		return attribute != null ? attribute : getName();
	}

	public Object getValueInternal(HttpServletRequest request) {
		Object value = request.getParameter(getName());
		if (value == null) {
			value = request.getAttribute(getAttribute());
		} else if (((String) value).length() == 0) {
			return null;
		}
		return value;
	}
	
}

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
package org.riotfamily.forms.request;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class SimpleFormRequest implements FormRequest {

	private Map params;
	
	public SimpleFormRequest(Map params) {
		this.params = params != null ? params : Collections.EMPTY_MAP;
	}

	public MultipartFile getFile(String name) {
		Object value = params.get(name);
		if (value instanceof MultipartFile) {
			return (MultipartFile) value;
		}
		return null;
	}

	public String getParameter(String name) {
		Object value = params.get(name);
		if (value instanceof String[]) {
			String[] values = (String[]) value;
			return values.length > 0 ? values[0] : null;
		}
		if (value instanceof Collection) {
			Iterator it  = ((Collection) value).iterator();
			return it.hasNext() ? (String) it.next() : null;
		}
		if (value instanceof String) {
			return (String) value;
		}
		return null;
	}

	public String[] getParameterValues(String name) {
		Object value = params.get(name);
		if (value instanceof String[]) {
			return (String[]) value;
		}
		if (value instanceof Collection) {
			return StringUtils.toStringArray((Collection) value);
		}
		if (value instanceof String) {
			return new String[] { (String) value };
		}
		return null;
	}

}

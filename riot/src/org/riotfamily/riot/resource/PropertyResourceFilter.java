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
package org.riotfamily.riot.resource;

import java.io.FilterReader;
import java.io.Reader;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.io.PropertyFilterReader;
import org.riotfamily.common.util.Generics;
import org.springframework.web.servlet.support.RequestContextUtils;

public class PropertyResourceFilter extends AbstractPathMatchingResourceFilter {

	public static final String CONTEXT_PATH_PROPERTY = "contextPath";

	public static final String LANGUAGE_PROPERTY = "language";

	private Map<String, String> properties;
	
	private boolean exposeContextPath = true;
	
	private boolean exposeLanguage = true;
	
	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}
		
	public void setExposeContextPath(boolean exposeContextPath) {
		this.exposeContextPath = exposeContextPath;
	}
	
	public void setExposeLanguage(boolean exposeLanguage) {
		this.exposeLanguage = exposeLanguage;
	}

	public FilterReader createFilterReader(Reader in, HttpServletRequest request) {
		Map<String, String> props = Generics.newHashMap(properties);
		if (exposeContextPath) {
			props.put(CONTEXT_PATH_PROPERTY, request.getContextPath());
		}
		if (exposeLanguage) {
			props.put(LANGUAGE_PROPERTY,
					RequestContextUtils.getLocale(request).getLanguage().toLowerCase());
		}
		return new PropertyFilterReader(in, props);
	}

}

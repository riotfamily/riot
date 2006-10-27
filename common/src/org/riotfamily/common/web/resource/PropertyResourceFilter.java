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
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.web.resource;

import java.io.FilterReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.io.PropertyFilterReader;
import org.springframework.util.Assert;

public class PropertyResourceFilter extends AbstractPathMatchingResourceFilter {

	public static final String CONTEXT_PATH_PROPERTY = "contextPath";
	
	private Properties properties;
	
	private boolean exposeContextPath = true;
	
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	public void setPropertiesMap(Map map) {
		properties = new Properties();
		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Assert.isInstanceOf(String.class, entry.getKey(), 
					"Map must only contain String keys.");
			
			Assert.isInstanceOf(String.class, entry.getValue(), 
					"Map must only contain String values.");
			
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			properties.setProperty(key, value);
		}
	}
	
	public void setExposeContextPath(boolean exposeContextPath) {
		this.exposeContextPath = exposeContextPath;
	}

	public FilterReader createFilterReader(Reader in, HttpServletRequest request) {
		Properties props = properties;
		if (exposeContextPath) {
			props = new Properties(properties);
			props.setProperty(CONTEXT_PATH_PROPERTY, request.getContextPath());
		}
		return new PropertyFilterReader(in, props);
	}

}

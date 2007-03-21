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
package org.riotfamily.components.property;

import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

/**
 * PropertyProcessor that initializes properties with the specified default 
 * values.
 */
public class DefaultValuePropertyProcessor extends PropertyProcessorAdapter {

	private Properties values;
	
	public void setValues(Properties values) {
		this.values = values;
	}

	/**
	 * Iterates over the defaults and checks whether a value is already set.
	 * If no matching entry is found in the map, the default value is added
	 * to the map. 
	 */
	public void resolveStrings(Map map) {
		if (values != null) {
			Enumeration en = values.propertyNames();
			while (en.hasMoreElements()) {
				String prop = (String) en.nextElement();
				if (!map.containsKey(prop)) {
					map.put(prop, values.getProperty(prop));
				}
			}
		}
	}

}

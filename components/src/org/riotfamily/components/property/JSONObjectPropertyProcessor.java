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
package org.riotfamily.components.property;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class JSONObjectPropertyProcessor extends PropertyProcessorAdapter
		implements PropertyProcessorRegistry {

	private Class beanClass;
	
	private Map propertyProcessors = new HashMap();

	public void setBeanClass(Class beanClass) {
		this.beanClass = beanClass;
	}
	
	public void registerPropertyProcessor(String property, 
			PropertyProcessor processor) {
		
		propertyProcessors.put(property, processor);
	}
	
	protected PropertyProcessor getProcessor(String key) {
		return (PropertyProcessor) propertyProcessors.get(key);
	}
	
	public String convertToString(Object object) {
		JSONObject json = new JSONObject();
		Map map = (Map) object;
		Iterator it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String key = (String) entry.getKey();
			PropertyProcessor processor = getProcessor(key);
			Object simpleObject;
			if (processor != null) {
				simpleObject = processor.toJSON(entry.getValue());
			}
			else {
				simpleObject = entry.getValue();
			}
			json.put(key, simpleObject);
		}
		return json.toString();
	}
	
	public Object resolveString(String s) {
		Map json = JSONObject.fromObject(s);
		return fromJSON(json);
	}
	
	public Object toJSON(Object object) {
		return object;
	}
	
	public Object fromJSON(Object object) {
		JSONObject json = (JSONObject) object;

		Iterator it = json.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String key = (String) entry.getKey();
			PropertyProcessor processor = getProcessor(key);
			if (processor != null) {
				json.element(key, processor.fromJSON(entry.getValue()));
			}
		}
		if (beanClass != null && !Map.class.isAssignableFrom(beanClass)) {
			return JSONObject.toBean(json, beanClass);
		}
		
		// Build HashMap and set JSONNull's to null
		HashMap result = new HashMap();
		it = json.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Object value = entry.getValue();
			if (JSONNull.getInstance() == value) {
				value = null;
			}
			result.put(entry.getKey(), value);
		}
		return result;
	}

}

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.sf.json.JSONArray;

import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class JSONArrayPropertyProcessor extends PropertyProcessorAdapter 
		implements PropertyProcessorRegistry {
	
	private Class collectionClass;
	
	private PropertyProcessor itemProcessor;
	
	public void setCollectionClass(Class collectionClass) {
		this.collectionClass = collectionClass;
	}
	
	public void registerPropertyProcessor(String property, 
			PropertyProcessor processor) {
		
		Assert.isNull(property, "Item editor must not be bound to a property.");
		Assert.isNull(itemProcessor, "List must not have more than one PropertyProcessor.");
		itemProcessor = processor;
	}
	
	public String convertToString(Object object) {
		if (object == null) {
			return null;
		}
		return toJSON(object).toString();
	}
	
	public Object resolveString(String s) {
		if (s == null) {
			return null;
		}
		JSONArray json = JSONArray.fromObject(s);
		return fromJSON(json);
	}
	
	public Object toJSON(Object object) {
		JSONArray array = new JSONArray();
		Collection c = (Collection) object;
		if (itemProcessor != null) {
			Iterator it = c.iterator();
			while (it.hasNext()) {
				Object item = it.next();
				array.element(itemProcessor.toJSON(item));
			}
		}
		else {
			array.addAll(c);
		}
		return array;
	}
	
	public Object fromJSON(Object object) {
		JSONArray array = (JSONArray) object;
		Collection result = createCollection();
		Iterator it = array.iterator();
		while (it.hasNext()) {
			Object item = it.next();
			result.add(itemProcessor.fromJSON(item));
		}
		return result;
	}
	
	private Collection createCollection() {
		if (collectionClass != null) {
			return (Collection) BeanUtils.instantiateClass(collectionClass);
		}
		return new ArrayList();
	}

}

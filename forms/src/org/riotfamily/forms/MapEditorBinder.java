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
package org.riotfamily.forms;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.core.GenericCollectionTypeResolver;
import org.springframework.util.Assert;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class MapEditorBinder extends AbstractEditorBinder {

	private Map map;
	
	private Class mapClass = HashMap.class;
	
	private Class valueClass = null;

	private boolean editingExisitingMap;
	
	public MapEditorBinder(Map map) {
		Assert.notNull(map);
		this.map = map;
		this.mapClass = map.getClass();
		this.valueClass = GenericCollectionTypeResolver.getMapValueType(mapClass);
		editingExisitingMap = true;
	}
	
	public MapEditorBinder(Class mapClass) {
		Assert.isAssignable(Map.class, mapClass);
		this.mapClass = mapClass; 
		this.valueClass = GenericCollectionTypeResolver.getMapValueType(mapClass);
		this.map = (Map) BeanUtils.instantiateClass(mapClass);
	}

	public void setBackingObject(Object backingObject) {
		if (backingObject != null) {
			Assert.isInstanceOf(mapClass, backingObject);
			map = (Map) backingObject;
			editingExisitingMap = true;
		}
		else {
			map.clear();
			editingExisitingMap = false;
		}
	}
	
	public boolean isEditingExistingBean() {
		return editingExisitingMap;
	}
	
	public Object getBackingObject() {
		return map;
	}

	public Class getBeanClass() {
		return mapClass;
	}

	public Class getPropertyType(String propertyName) {
		if (valueClass != null) {
			return valueClass;
		}
		Object value = getPropertyValue(propertyName);
		if (value != null) {
			return value.getClass();
		}
		Editor editor = findEditorByProperty(propertyName);
		if (editor != null) {
			value = editor.getValue();
			if (value != null) {
				return value.getClass();
			}
		}
		return Object.class;
	}
	
	public Object getPropertyValue(String propertyName) {
		return map.get(propertyName);
	}

	public void setPropertyValue(String propertyName, Object value) {
		map.put(propertyName, value);
	}

}

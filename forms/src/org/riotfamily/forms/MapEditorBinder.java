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

import java.util.Map;

import org.riotfamily.common.util.SpringUtils;
import org.springframework.core.GenericCollectionTypeResolver;
import org.springframework.util.Assert;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class MapEditorBinder extends AbstractEditorBinder {

	private Map<Object,Object> map;
	
	@SuppressWarnings("unchecked")
	private Class<? extends Map> mapClass;
	
	private Class<?> valueClass = null;

	private boolean editingExisitingMap;
	
	public MapEditorBinder() {
		mapClass = Map.class;
	}
			
	public MapEditorBinder(Map<Object, Object> map) {
		Assert.notNull(map);
		this.map = map;
		this.mapClass = map.getClass();
		this.valueClass = GenericCollectionTypeResolver.getMapValueType(mapClass);
		editingExisitingMap = true;
	}
	
	@SuppressWarnings("unchecked")
	public MapEditorBinder(Class<? extends Map> mapClass) {
		this.mapClass = mapClass; 
		this.valueClass = GenericCollectionTypeResolver.getMapValueType(mapClass);
		this.map = SpringUtils.newInstance(mapClass);
	}

	@SuppressWarnings("unchecked")
	public void setBackingObject(Object backingObject) {
		if (backingObject != null) {
			Assert.isInstanceOf(mapClass, backingObject);
			map = (Map<Object, Object>) backingObject;
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

	public Class<?> getBeanClass() {
		return mapClass;
	}

	public Class<?> getPropertyType(String propertyName) {
		Object value = getPropertyValue(propertyName);
		if (value != null) {
			return value.getClass();
		}
		if (valueClass != null) {
			return valueClass;
		}
		return Object.class;
	}
	
	public Object getPropertyValue(String propertyName) {
		return map != null ? map.get(propertyName) : null;
	}

	public void setPropertyValue(String propertyName, Object value) {
		map.put(propertyName, value);
	}

}

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
package org.riotfamily.forms.element.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.riotfamily.common.util.PropertyUtils;
import org.riotfamily.forms.bind.Editor;
import org.riotfamily.forms.element.support.Container;
import org.riotfamily.forms.element.support.TemplateElement;
import org.riotfamily.forms.factory.ElementFactory;
import org.springframework.util.Assert;


/**
 * A list widget to edit maps.
 */
public class MapEditor extends Container implements Editor {
	
	/** Class to use for newly created maps */
	private Class mapClass = HashMap.class;
	
	private String keyCollectionProperty;
	
	private String labelProperty;
	
	/** Factory to create elements for newly added items */
	private ElementFactory itemElementFactory;
	
	
	public void setKeyCollectionProperty(String keyCollectionProperty) {
		this.keyCollectionProperty = keyCollectionProperty;
	}


	public void setLabelProperty(String labelProperty) {
		this.labelProperty = labelProperty;
	}


	/**
	 * Sets the class to use if a new map instance needs to be created.
	 * Default is <code>java.util.HashMap</code>.
	 *   
	 * @param mapClass the class to use for new collections
	 */
	public void setMapClass(Class mapClass) {
		Assert.isAssignable(Map.class, mapClass);
		this.mapClass = mapClass;
	}	
	
	/**
	 * Sets the factory that is used to create an element for each map value. 
	 */
	public void setItemElementFactory(ElementFactory itemElementFactory) {
		this.itemElementFactory = itemElementFactory;
	}
	
	/**
	 * 
	 */
	public void setValue(Object value) {
		Collection keys = null;
		if (keyCollectionProperty != null) {
			keys = (Collection) getEditorBinding().getEditorBinder()
					.getPropertyValue(keyCollectionProperty);
		}
		if (value != null) {
			if (!(value instanceof Map)) {
				throw new IllegalArgumentException("Value must implement " +
						"the java.util.Map interface");
			}
			Map map = (Map) value;
			Iterator it = map.entrySet().iterator();			
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				Object key = entry.getKey();
				if (keys == null || keys.contains(key)) {
					addItem(key, entry.getValue());
					if (keys != null) {
						keys.remove(key);
					}
				}
			}
			if (keys != null) {
				it = keys.iterator();
				while (it.hasNext()) {
					addItem(it.next(), null);
				}
			}
		}
	}
	
	public Object getValue() {
		Map map = createOrClearMap();
		Iterator it = getElements().iterator();
		while (it.hasNext()) {
			MapItem item = (MapItem) it.next();
			map.put(item.getKey(), item.getValue());
		}
		return map;
	}
				
	private Map createOrClearMap() {
		Map map = (Map) getEditorBinding().getValue();
		if (map == null) {
			try {
				map = (Map) mapClass.newInstance();
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		else {
			map.clear();
		}
		return map;
	}

	protected void addItem(Object key, Object value) {		
		MapItem item = new MapItem(key);
		addElement(item);
		item.focus();
		item.setValue(value);
	}
					
	public class MapItem extends TemplateElement {
		
		private Object key;
		
		private Editor element;
		
		public MapItem(Object key) {
			super("item");
			this.key = key;
			setSurroundBySpan(false);
			element = (Editor) itemElementFactory.createElement(this, getForm());
			element.setRequired(true);
			addComponent("element", element);
		}
		
		public String getLabel() {
			if (labelProperty != null) {
				return PropertyUtils.getPropertyAsString(key, labelProperty);
			}
			return key.toString();
		}
		
		public Editor getElement() {
			return element;
		}
		
		public Object getKey() {
			return this.key;
		}
		
		public Object getValue() {
			return element.getValue();
		}
		
		public void setValue(Object value) {
			element.setValue(value);
		}

		public void focus() {
			element.focus();
		}
				
	}
	
}

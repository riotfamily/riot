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
package org.riotfamily.forms.element.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.riotfamily.common.beans.PropertyUtils;
import org.riotfamily.forms.Container;
import org.riotfamily.forms.Editor;
import org.riotfamily.forms.ElementFactory;
import org.riotfamily.forms.ErrorUtils;
import org.riotfamily.forms.TemplateUtils;
import org.riotfamily.forms.element.TemplateElement;
import org.riotfamily.forms.element.select.SelectElement;
import org.riotfamily.forms.event.Button;
import org.riotfamily.forms.event.ClickEvent;
import org.riotfamily.forms.event.ClickListener;
import org.riotfamily.forms.options.OptionsModel;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;


/**
 * A list widget to edit maps.
 */
public class MapEditor extends TemplateElement implements Editor {
	
	/** Class to use for newly created maps */
	private Class mapClass = HashMap.class;
	
	private Object keyOptionsModel;
	
	private String labelProperty;
	
	private Container items = new Container();

	private ElementFactory keyElementFactory;
	
	private Editor keyEditor;
	
	/** Factory to create elements for newly added items */
	private ElementFactory itemElementFactory;
	
	
	public MapEditor() {
		addComponent("items", items);
		setTemplate(TemplateUtils.getTemplatePath(MapEditor.class));
	}
	
	public void setKeyOptionsModel(Object keyOptionsModel) {
		this.keyOptionsModel = keyOptionsModel;
	}

	public void setLabelProperty(String labelProperty) {
		this.labelProperty = labelProperty;
	}
	
	/**
	 * Sets the factory that is used to create an element for each map key. 
	 */
	public void setKeyElementFactory(ElementFactory keyElementFactory) {
		this.keyElementFactory = keyElementFactory;
	}
	
	protected void initCompositeElement() {
		if (keyElementFactory != null) {
			keyEditor = (Editor) keyElementFactory.createElement(this, getForm(), false);
			keyEditor.setFieldName(getFieldName() + ".add");
			if (keyEditor instanceof SelectElement) {
				SelectElement se = (SelectElement) keyEditor;
				se.setOptionsModel(new MapOptionsModel(se));
			}
			addComponent("keyEditor", keyEditor);
			
			Button addButton = new Button();
			addButton.setLabelKey("label.form.map.add");
			addButton.setLabel("Add");
			addButton.setPartitialSubmit(getId());
			addButton.addClickListener(new ClickListener() {
				public void clicked(ClickEvent event) {
					Object key = keyEditor.getValue();
					if (key == null) {
						ErrorUtils.reject(keyEditor, "map.emptyKey");
					}
					else if (getKeys().contains(key)) {
						ErrorUtils.reject(keyEditor, "map.duplicateKey");
					}
					else {
						addItem(key, null);
						keyEditor.setValue(null);
						getFormListener().elementChanged(keyEditor);
					}
				}
			});
			addComponent("addButton", addButton);
		}
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
		Map map = null;
		if (value != null) {
			Assert.isInstanceOf(Map.class, value, "Value must implement the Map interface");
			map = (Map) value;
		}
		
		Collection keys = null;
		if (keyOptionsModel != null) {
			keys = getForm().getOptionValues(keyOptionsModel);
		}
		else if (map != null) {
			keys = map.keySet();
		}
		
		if (keys != null) {
			Iterator it = keys.iterator();			
			while (it.hasNext()) {
				Object key = it.next();
				Object obj = map != null ? map.get(key) : null;
				addItem(key, obj);			
			}
		}
	}
	
	public Object getValue() {
		Map map = createOrClearMap();
		Iterator it = items.getElements().iterator();
		while (it.hasNext()) {
			MapItem item = (MapItem) it.next();
			map.put(item.getKey(), item.getValue());
		}
		return map;
	}
				
	private Map createOrClearMap() {
		Map map = (Map) getEditorBinding().getValue();
		if (map == null) {
			map = (Map) BeanUtils.instantiateClass(mapClass);
		}
		else {
			map.clear();
		}
		return map;
	}

	private Set getKeys() {
		HashSet keys = new HashSet();
		Iterator it = items.getElements().iterator();
		while (it.hasNext()) {
			MapItem item = (MapItem) it.next();
			keys.add(item.getKey());
		}
		return keys;
	}
	
	protected void addItem(Object key, Object value) {		
		MapItem item = new MapItem(key, keyEditor != null);
		items.addElement(item);
		item.focus();
		item.setValue(value);
	}
	
	protected void removeItem(MapItem item) {
		items.removeElement(item);
		if (keyEditor != null) {
			ErrorUtils.removeErrors(keyEditor);
			getFormListener().elementChanged(keyEditor);
		}
	}
					
	public class MapItem extends TemplateElement {
		
		private Object key;
		
		private Editor element;
		
		private Button removeButton;
		
		public MapItem(Object key, boolean removable) {
			super("item");
			this.key = key;
			setSurroundByDiv(false);
			element = (Editor) itemElementFactory.createElement(this, getForm(), false);
			addComponent("element", element);
			if (removable) {
				removeButton = new Button();
				removeButton.setLabelKey("label.form.map.remove");
				removeButton.setLabel("Remove");
				removeButton.setTabIndex(2);
				removeButton.addClickListener(new ClickListener() {
					public void clicked(ClickEvent event) {					
						removeItem(MapItem.this);
					}
				});	
				addComponent("removeButton", removeButton);
			}
		}
		
		public String getLabel() {
			if (labelProperty != null) {
				return PropertyUtils.getPropertyAsString(key, labelProperty);
			}
			return String.valueOf(key);
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
			element.setEditorBinding(new CollectionItemEditorBinding(element, value));
			element.setValue(value);
		}
		
		public void focus() {
			element.focus();
		}
				
	}
	
	private class MapOptionsModel implements OptionsModel {
		
		private Object model;
		
		public MapOptionsModel(SelectElement keyElement) {
			this.model = keyElement.getOptionsModel();
		}

		public Collection getOptionValues() {
			Collection keys = getKeys();
			ArrayList result = new ArrayList();
			Iterator it = getForm().getOptionValues(model).iterator();
			while (it.hasNext()) {
				Object key = (Object) it.next();
				if (!keys.contains(key)) {
					result.add(key);
				}
			}
			return result;
		}
	}
}

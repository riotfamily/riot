/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.forms.element.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.riotfamily.common.beans.property.PropertyUtils;
import org.riotfamily.common.ui.ObjectRenderer;
import org.riotfamily.common.ui.StringRenderer;
import org.riotfamily.common.util.Generics;
import org.riotfamily.forms.Container;
import org.riotfamily.forms.Editor;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.ElementFactory;
import org.riotfamily.forms.ErrorUtils;
import org.riotfamily.forms.NestedEditor;
import org.riotfamily.forms.TemplateUtils;
import org.riotfamily.forms.element.TemplateElement;
import org.riotfamily.forms.element.select.SelectElement;
import org.riotfamily.forms.event.Button;
import org.riotfamily.forms.event.ClickEvent;
import org.riotfamily.forms.event.ClickListener;
import org.riotfamily.forms.options.OptionsModel;
import org.riotfamily.forms.options.OptionsModelUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;


/**
 * A list widget to edit maps.
 */
public class MapEditor extends TemplateElement implements Editor, NestedEditor {
	
	/** Class to use for newly created maps */
	private Class<?> mapClass = HashMap.class;
	
	private Object keyOptionsModel;
	
	private String labelProperty;
	
	private ObjectRenderer labelRenderer;
	
	private boolean keepNullValues;
	
	private boolean removeUnknownKeys;
	
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
	
	public void setLabelRenderer(ObjectRenderer labelRenderer) {
		this.labelRenderer = labelRenderer;
	}
	
	public void setKeepNullValues(boolean keepNullValues) {
		this.keepNullValues = keepNullValues;
	}

	public void setRemoveUnknownKeys(boolean removeUnknownKeys) {
		this.removeUnknownKeys = removeUnknownKeys;
	}
	
	@Override
	protected void afterFormContextSet() {
		super.afterFormContextSet();
		items.setComponentPadding(getFormContext().getSizing().getMapItemPadding());
	}
	
	/**
	 * Sets the factory that is used to create an element for each map key. 
	 */
	public void setKeyElementFactory(ElementFactory keyElementFactory) {
		this.keyElementFactory = keyElementFactory;
	}
	
	@Override
	protected void initCompositeElement() {
		if (keyElementFactory != null) {
			keyEditor = (Editor) keyElementFactory.createElement(this, getForm(), false);
			keyEditor.setFieldName(getFieldName() + ".add");
			if (keyEditor instanceof SelectElement) {
				SelectElement se = (SelectElement) keyEditor;
				se.setOptions(new MapOptionsModel(se));
			}
			addComponent("keyEditor", keyEditor);
			
			if (isEnabled()) {
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
							addItem(key, null, true);
							keyEditor.setValue(null);
							getFormListener().elementChanged(keyEditor);
						}
					}
				});
				addComponent("addButton", addButton);
			}
		}
	}
	
	/**
	 * Sets the class to use if a new map instance needs to be created.
	 * Default is <code>java.util.HashMap</code>.
	 *   
	 * @param mapClass the class to use for new collections
	 */
	public void setMapClass(Class<?> mapClass) {
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
		Map<?,?> map = null;
		if (value != null) {
			Assert.isInstanceOf(Map.class, value, "Value must implement the Map interface");
			map = (Map<?,?>) value;
		}
		
		Collection<?> keys = null;
		if (keyOptionsModel != null) {
			keys = OptionsModelUtils.getOptionValues(keyOptionsModel, this);
		}
		else if (map != null) {
			keys = map.keySet();
		}
		
		if (keys != null) {
			Iterator<?> it = keys.iterator();			
			while (it.hasNext()) {
				Object key = it.next();
				Object obj = map != null ? map.get(key) : null;
				addItem(key, obj, false);			
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public Object getValue() {
		Map map = createOrClearMap();
		Iterator<Element> it = items.getElements().iterator();
		while (it.hasNext()) {
			MapItem item = (MapItem) it.next();
			Object value = item.getValue();
			if (value != null || keepNullValues) {
				map.put(item.getKey(), value);
			}
			else {
				map.remove(item.getKey());
			}
		}
		return map;
	}
	
	public Editor getEditor(String property) {
		if (property != null) {
			int i = property.indexOf('.');
			if (i != -1) {
				String nested = property.substring(i + 1);
				String id = property = property.substring(0, i);
				Editor editor = (Editor) getForm().getElementById(id); 
				if (editor instanceof NestedEditor) {
					NestedEditor ne = (NestedEditor) editor;
					return ne.getEditor(nested);
				}
				else {
					throw new IllegalStateException("Editor for " + property 
							+ " must implement the NestedEditor interface");
				}
			}
			return (Editor) getForm().getElementById(property);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private Map createOrClearMap() {
		Map map = (Map) getEditorBinding().getValue();
		if (map == null) {
			map = (Map) BeanUtils.instantiateClass(mapClass);
		}
		else if (removeUnknownKeys) {
			map.clear();
		}
		return map;
	}

	private Set<Object> getKeys() {
		HashSet<Object> keys = Generics.newHashSet();
		Iterator<Element> it = items.getElements().iterator();
		while (it.hasNext()) {
			MapItem item = (MapItem) it.next();
			keys.add(item.getKey());
		}
		return keys;
	}
	
	protected void addItem(Object key, Object value, boolean newItem) {		
		MapItem item = new MapItem(key, keyEditor != null && isEnabled());
		items.addElement(item);
		item.focus();
		item.setValue(value, newItem);
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
		
		private Editor editor;
		
		private Button removeButton;
		
		private CollectionItemEditorBinding binding;
		
		public MapItem(Object key, boolean removable) {
			super("item");
			this.key = key;
			binding = new CollectionItemEditorBinding(MapEditor.this.getEditorBinding());
			setWrap(false);
			editor = (Editor) itemElementFactory.createElement(this, getForm(), false);
			editor.setEditorBinding(binding);
			binding.setEditor(editor);
			addComponent("editor", editor);
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
		
		public void renderLabel() {
			Object label = key;
			if (labelProperty != null) {
				label = PropertyUtils.getProperty(key, labelProperty);
			}
			if (labelRenderer == null) {
				labelRenderer = new StringRenderer();
			}
			labelRenderer.render(label, getFormContext(), getFormContext().getWriter());
		}
		
		public Editor getEditor() {
			return editor;
		}
		
		public Object getKey() {
			return this.key;
		}
		
		public Object getValue() {
			return editor.getValue();
		}
		
		public void setValue(Object value, boolean newItem) {
			binding.setExistingItem(!newItem);
			binding.setValue(value);
			editor.setValue(value);
		}

		@Override
		public void focus() {
			editor.focus();
		}
				
	}
	
	private class MapOptionsModel implements OptionsModel {
		
		private Object model;
		
		public MapOptionsModel(SelectElement keyElement) {
			this.model = keyElement.getOptions();
		}

		public Collection<?> getOptionValues(Element element) {
			Collection<?> keys = getKeys();
			ArrayList<Object> result = Generics.newArrayList();
			Iterator<?> it = OptionsModelUtils.getOptionValues(model, element).iterator();
			while (it.hasNext()) {
				Object key = it.next();
				if (!keys.contains(key)) {
					result.add(key);
				}
			}
			return result;
		}
	}
}

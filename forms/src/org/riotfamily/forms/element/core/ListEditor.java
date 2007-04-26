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
package org.riotfamily.forms.element.core;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.riotfamily.common.util.PropertyUtils;
import org.riotfamily.forms.FormRequest;
import org.riotfamily.forms.bind.Editor;
import org.riotfamily.forms.bind.EditorBinder;
import org.riotfamily.forms.bind.EditorBinding;
import org.riotfamily.forms.element.DHTMLElement;
import org.riotfamily.forms.element.support.Container;
import org.riotfamily.forms.element.support.TemplateElement;
import org.riotfamily.forms.error.ErrorUtils;
import org.riotfamily.forms.event.ClickEvent;
import org.riotfamily.forms.event.ClickListener;
import org.riotfamily.forms.factory.ElementFactory;
import org.riotfamily.forms.resource.FormResource;
import org.riotfamily.forms.resource.ResourceElement;
import org.riotfamily.forms.resource.Resources;
import org.riotfamily.forms.support.TemplateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;


/**
 * A list widget to edit lists and sets.
 */
public class ListEditor extends TemplateElement implements Editor, 
		ResourceElement, DHTMLElement {
		
	/** Class to use for newly created collections */
	private Class collectionClass;
		
	private String parentProperty;
	
	/** Factory to create elements for newly added items */
	private ElementFactory itemElementFactory;
	
	/** Container of ListItems */
	private Container items = new Container();
	
	/** Button to add an item to the list */
	private Button addButton;
	
	private int minSize;
	
	private int maxSize;
		
	private boolean sortable;
	
	public ListEditor() {
		addComponent("items", items);
		addButton = new Button();
		addButton.setLabelKey("form.dynamicList.add");
		addButton.setLabel("Add");
		addButton.addClickListener(new ClickListener() {
			public void clicked(ClickEvent event) {
				addItem();				
			}
		});
		addComponent("addButton", addButton);
		setTemplate(TemplateUtils.getTemplatePath(ListEditor.class));
	}
	
	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}
	
	public boolean isSortable() {
		return sortable;
	}
	
	public void setMinSize(int minSize) {
		this.minSize = minSize;
	}
	
	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}
	
	public void setRequired(boolean required) {
		minSize = required ? 1 : 0;
	}
	
	public boolean isRequired() {
		return minSize != 0;
	}

	public void setParentProperty(String parentProperty) {
		this.parentProperty = parentProperty;
	}

	public FormResource getResource() {
		return sortable ? Resources.SCRIPTACULOUS_DRAG_DROP : null;
	}
	
	/**
	 * Sets the class to use if a new collection instance needs to be created.
	 * If no class is set, a suitable implementation will be selected according
	 * to the type of the property the element is bound to.
	 *   
	 * @param collectionClass the class to use for new collections
	 */
	public void setCollectionClass(Class collectionClass) {
		this.collectionClass = collectionClass;
	}	
	
	/**
	 * Sets the factory that is used to create an element for each list item. 
	 */
	public void setItemElementFactory(ElementFactory itemElementFactory) {
		this.itemElementFactory = itemElementFactory;
	}
	
	/**
	 * 
	 */
	public void setValue(Object value) {
		if (collectionClass == null) {
			Class type = getEditorBinding().getPropertyType();
			if (type.isInterface()) {
				if (Set.class.isAssignableFrom(type)) {
					collectionClass = HashSet.class;
				}
				else {
					collectionClass = ArrayList.class;
				}
			}
			else {
				collectionClass = type;
			}
		}
		if (value != null) {
			if (!(value instanceof Collection)) {
				throw new IllegalArgumentException("Value must implement " +
						"the java.util.Collection interface");
			}
			Collection collection = (Collection) value;
			Iterator it = collection.iterator();			
			while (it.hasNext()) {
				ListItem item = addItem();
				item.setValue(it.next());
			}
		}
	}
	
	public Object getValue() {
		Collection collection = createOrClearCollection();
		Iterator it = items.getElements().iterator();
		while (it.hasNext()) {
			ListItem item = (ListItem) it.next();
			if (item.getElement().getValue() != null) {
				Object value = item.getElement().getValue();
				if (parentProperty != null) {
					PropertyUtils.setProperty(value, parentProperty, 
							getEditorBinding().getEditorBinder().getBackingObject());
				}
				collection.add(value);
			}
		}
		return collection;
	}
				
	private Collection createOrClearCollection() {
		Collection collection = (Collection) getEditorBinding().getValue();
		if (collection == null) {
			collection = (Collection) BeanUtils.instantiateClass(collectionClass);
		}
		else {
			collection.clear();
		}
		return collection;
	}

	protected ListItem addItem() {		
		ListItem item = new ListItem();
		items.addElement(item);
		item.focus();
		
		item.setValue(null);
		
		return item;
	}
	
	protected void removeItem(ListItem item) {		
		items.removeElement(item);				
	}	
	
	protected void validate() {
		int size = ((Collection) getValue()).size();
		if (minSize > 0 && size < minSize) {
			ErrorUtils.reject(this, "list.size.tooSmall", 
					new Object[] {new Integer(minSize)});
		}
		if (maxSize > 0 && size > maxSize) {
			ErrorUtils.reject(this, "list.size.tooLarge", 
					new Object[] {new Integer(maxSize)});
		}
	}
	
	public Container getItems() {
		return this.items;
	}
	
	public String getInitScript() {
		if (sortable && !items.isEmpty()) {
			return TemplateUtils.getInitScript(this);
		}
		else {
			return null;
		}
	}
		
	protected void processRequestInternal(FormRequest request) {
		if (sortable) {
			String itemOrder = request.getParameter(getParamName());
			if (StringUtils.hasLength(itemOrder)) {
				Collections.sort(items.getElements(), 
						new ListItemComparator(itemOrder));
			}
		}
	}
	
	public class ListItem extends TemplateElement implements DHTMLElement {
		
		private Editor element;
		
		private Button removeButton;	
				
		public ListItem() {
			super("item");
			setSurroundBySpan(false);
			element = (Editor) itemElementFactory.createElement(this, getForm());
			element.setRequired(true);
			removeButton = new Button();
			removeButton.setLabelKey("form.dynamicList.remove");
			removeButton.setLabel("Remove");
			removeButton.setTabIndex(2);
			removeButton.addClickListener(new ClickListener() {
				public void clicked(ClickEvent event) {					
					removeItem(ListItem.this);
				}
			});	
			addComponent("element", element);
			addComponent("removeButton", removeButton);
		}
		
		public Editor getElement() {
			return element;
		}
		
		public void focus() {
			element.focus();
		}
		
		public boolean isShowDragHandle() {
			return isSortable();
		}
		
		public void setValue(Object value) {
			element.setEditorBinding(new ListItemEditorBinding(element, value));
			element.setValue(value);
		}
		
		public String getInitScript() {
			if (getForm().isRendering()) {
				return null;
			}
			return ListEditor.this.getInitScript();
		}
		
	}
	
	private class ListItemComparator implements Comparator {

		private String itemOrder;
		
		public ListItemComparator(String itemOrder) {
			this.itemOrder = itemOrder;
		}

		public int compare(Object obj1, Object obj2) {
			ListItem item1 = (ListItem) obj1;
			ListItem item2 = (ListItem) obj2;
			int pos1 = itemOrder.indexOf(item1.getId() + ',');
			int pos2 = itemOrder.indexOf(item2.getId() + ',');
			return pos1 - pos2;
		}
		
	}
	
	private class ListItemEditorBinding implements EditorBinding {

		private Editor editor;
		
		private Object value;
		
		public ListItemEditorBinding(Editor editor, Object value) {
			this.editor = editor;
			this.value = value;
		}

		public Class getBeanClass() {
			return value != null ? value.getClass() : null;
		}

		public Editor getEditor() {
			return editor;
		}

		public EditorBinder getEditorBinder() {
			return null;
		}

		public String getProperty() {
			return null;
		}

		public PropertyEditor getPropertyEditor() {
			return null;
		}

		public String getPropertyPath() {
			return "[" + editor.getId() + "]";
		}

		public Class getPropertyType() {
			return Object.class;
		}

		public Object getValue() {
			return value;
		}
		
		
	}
}

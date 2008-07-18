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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.riotfamily.common.beans.PropertyUtils;
import org.riotfamily.common.util.Generics;
import org.riotfamily.forms.Container;
import org.riotfamily.forms.DHTMLElement;
import org.riotfamily.forms.Editor;
import org.riotfamily.forms.ElementFactory;
import org.riotfamily.forms.ErrorUtils;
import org.riotfamily.forms.NestedEditor;
import org.riotfamily.forms.TemplateUtils;
import org.riotfamily.forms.element.TemplateElement;
import org.riotfamily.forms.event.Button;
import org.riotfamily.forms.event.ClickEvent;
import org.riotfamily.forms.event.ClickListener;
import org.riotfamily.forms.request.FormRequest;
import org.riotfamily.forms.resource.FormResource;
import org.riotfamily.forms.resource.ResourceElement;
import org.riotfamily.forms.resource.Resources;
import org.riotfamily.forms.resource.ScriptResource;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;


/**
 * A list widget to edit lists and sets.
 */
public class ListEditor extends TemplateElement implements Editor, NestedEditor, 
		ResourceElement, DHTMLElement {
		
	/** Class to use for newly created collections */
	private Class<?> collectionClass;
		
	private String parentProperty;
	
	private String positionProperty;
	
	/** Factory to create elements for newly added items */
	ElementFactory itemElementFactory;
	
	List<ListItem> items = Generics.newArrayList();
	
	/** Container of ListItems */
	private Container container = new Container(items);
	
	/** Button to add an item to the list */
	private Button addButton;
		
	private int minSize;
	
	private int maxSize;
		
	private boolean sortable = false;
	
	private boolean dragAndDrop = true;
	
	public ListEditor() {
		addComponent("items", container);
		addButton = new Button();
		addButton.setLabelKey("label.form.list.add");
		addButton.setLabel("Add");
		addButton.addClickListener(new ClickListener() {
			public void clicked(ClickEvent event) {
				addItem(null, true).focus();				
			}
		});
		addComponent("addButton", addButton);
		setTemplate(TemplateUtils.getTemplatePath(ListEditor.class));
	}
	
	public void setSortable(boolean sortable) {
		this.sortable = sortable;
		if (!sortable) {
			dragAndDrop = false;
		}
	}
	
	public boolean isSortable() {
		return sortable;
	}
	
	public void setDragAndDrop(boolean dragAndDrop) {
		this.dragAndDrop = dragAndDrop;
		if (dragAndDrop) {
			sortable = true;
		}
	}
	
	public boolean isDragAndDrop() {
		return dragAndDrop;
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
	
	public void setPositionProperty(String positionProperty) {
		this.positionProperty = positionProperty;
	}

	public FormResource getResource() {
		if (sortable) {
			if (dragAndDrop) {
				return Resources.SCRIPTACULOUS_DRAG_DROP;
			}
			return new ScriptResource("form/listEditor.js", "initListEditor", Resources.PROTOTYPE);
		}
		return null;
	}
	
	/**
	 * Sets the class to use if a new collection instance needs to be created.
	 * If no class is set, a suitable implementation will be selected according
	 * to the type of the property the element is bound to.
	 *   
	 * @param collectionClass the class to use for new collections
	 */
	public void setCollectionClass(Class<?> collectionClass) {
		this.collectionClass = collectionClass;
	}
	
	/**
	 * Returns the class set via {@link #setCollectionClass(Class)}.
	 */
	public Class<?> getCollectionClass() {
		return this.collectionClass;
	}
	
	/**
	 * Sets the factory that is used to create an element for each list item. 
	 */
	public void setItemElementFactory(ElementFactory itemElementFactory) {
		this.itemElementFactory = itemElementFactory;
	}
	
	/**
	 * Returns the factory that is used to create elements for the list items.
	 */
	public ElementFactory getItemElementFactory() {
		return this.itemElementFactory;
	}
		
	/**
	 * 
	 */
	public void setValue(Object value) {
		if (collectionClass == null) {
			Class<?> type = getEditorBinding().getPropertyType();
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
			Collection<?> collection = (Collection<?>) value;
			Iterator<?> it = collection.iterator();			
			while (it.hasNext()) {
				addItem(it.next(), false);
			}
		}
	}
	
	public void setBackingObject(Object obj) {
		if (obj instanceof Collection) {
			List<?> newValues = Generics.newArrayList((Collection<?>) obj);
			for (ListItem item : items) {
				if (!item.isNew()) {
					Object oldValue = item.getBackingObject();
					int i = newValues.indexOf(oldValue);
					if (i >= 0) {
						item.setBackingObject(newValues.get(i));
					}
				}
			}
		}
	}
	
	public Object getValue() {
		Collection<Object> collection = createOrClearCollection();
		int i = 0;
		for (ListItem item : items) {
			Object value = item.getValue();
			if (value != null) {
				if (parentProperty != null) {
					PropertyUtils.setProperty(value, parentProperty, 
							getEditorBinding().getEditorBinder().getBackingObject());
				}
				if (positionProperty != null) {
					PropertyUtils.setProperty(value, positionProperty, String.valueOf(i++));
				}
				collection.add(value);
			}
		}
		return collection;
	}
				
	@SuppressWarnings("unchecked")
	private Collection<Object> createOrClearCollection() {
		Collection collection = (Collection) getEditorBinding().getValue();
		if (collection == null) {
			collection = (Collection) BeanUtils.instantiateClass(collectionClass);
		}
		else {
			collection.clear();
		}
		return collection;
	}

	protected ListItem addItem(Object value, boolean newItem) {		
		ListItem item = createItem();
		Editor editor = (Editor) itemElementFactory.createElement(
				item, getForm(), false);
		
		item.setEditor(editor);
		item.setValue(value, newItem);
		container.addElement(item);		
		return item;
	}
	
	protected ListItem createItem() {
		return new ListItem(this);
	}
	
	public void removeItem(ListItem item) {		
		container.removeElement(item);				
	}	
	
	protected void validate() {
		int size = container.getElements().size();
		if (minSize > 0 && size < minSize) {
			ErrorUtils.reject(this, "list.size.tooSmall", 
					new Object[] {new Integer(minSize)});
		}
		if (maxSize > 0 && size > maxSize) {
			ErrorUtils.reject(this, "list.size.tooLarge", 
					new Object[] {new Integer(maxSize)});
		}
	}
		
	public String getInitScript() {
		if (sortable && isEnabled() && !items.isEmpty()) {
			if (dragAndDrop) {
				return TemplateUtils.getInitScript(this, ListEditor.class);
			}
			return String.format("initListEditor('%s');", container.getId()); 
		}
		else {
			return null;
		}
	}
		
	protected void processRequestInternal(FormRequest request) {
		if (sortable) {
			String itemOrder = request.getParameter(getParamName());
			if (StringUtils.hasLength(itemOrder)) {
				Collections.sort(items,	new ListItemComparator(itemOrder));
			}
		}
		validate();
	}
}

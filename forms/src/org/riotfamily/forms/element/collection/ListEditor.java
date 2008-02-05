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
import org.riotfamily.forms.Container;
import org.riotfamily.forms.DHTMLElement;
import org.riotfamily.forms.Editor;
import org.riotfamily.forms.ElementFactory;
import org.riotfamily.forms.ErrorUtils;
import org.riotfamily.forms.TemplateUtils;
import org.riotfamily.forms.element.TemplateElement;
import org.riotfamily.forms.event.Button;
import org.riotfamily.forms.event.ClickEvent;
import org.riotfamily.forms.event.ClickListener;
import org.riotfamily.forms.request.FormRequest;
import org.riotfamily.forms.resource.FormResource;
import org.riotfamily.forms.resource.ResourceElement;
import org.riotfamily.forms.resource.Resources;
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
	ElementFactory itemElementFactory;
	
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
		addButton.setLabelKey("label.form.list.add");
		addButton.setLabel("Add");
		addButton.addClickListener(new ClickListener() {
			public void clicked(ClickEvent event) {
				addItem().focus();				
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
	 * Returns the class set via {@link #setCollectionClass(Class)}.
	 */
	public Class getCollectionClass() {
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
	
	protected List getListItems() {
		return items.getElements();
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
		Iterator it = getListItems().iterator();
		while (it.hasNext()) {
			ListItem item = (ListItem) it.next();
			Object value = item.getValue();
			if (value != null) {
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
		ListItem item = createItem();
		item.setEditor((Editor) itemElementFactory.createElement(
				item, getForm(), false));
		
		items.addElement(item);
		item.setValue(null);
		
		return item;
	}
	
	protected ListItem createItem() {
		return new ListItem(this);
	}
	
	public void removeItem(ListItem item) {		
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
		if (sortable && isEnabled() && !items.isEmpty()) {
			return TemplateUtils.getInitScript(this, ListEditor.class);
		}
		else {
			return null;
		}
	}
		
	protected void processRequestInternal(FormRequest request) {
		if (sortable) {
			String itemOrder = request.getParameter(getParamName());
			if (StringUtils.hasLength(itemOrder)) {
				Collections.sort(getListItems(), 
						new ListItemComparator(itemOrder));
			}
		}
		validate();
	}
}

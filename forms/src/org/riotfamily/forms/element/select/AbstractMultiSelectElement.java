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
package org.riotfamily.forms.element.select;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.riotfamily.common.util.Generics;
import org.riotfamily.common.util.SpringUtils;
import org.riotfamily.forms.ErrorUtils;
import org.riotfamily.forms.event.JavaScriptEvent;
import org.riotfamily.forms.request.FormRequest;

/**
 * Abstract superclass for elements that let the user choose from a set of
 * options like selectboxes or radio button groups.
 */
public abstract class AbstractMultiSelectElement 
		extends AbstractSelectElement {

	private List<Object> selectedValues = Generics.newArrayList();

	@SuppressWarnings("unchecked")
	private Class<? extends Collection> collectionClass;

	private Integer maxSelection;
	
	/**
	 * Sets the class to use if a new collection instance needs to be created.
	 * If no class is set a suitable implementation will be selected according
	 * to the type of the property the element is bound to.
	 * 
	 * @param collectionClass the class to use for new collections
	 */
	public void setCollectionClass(Class<? extends Collection<?>> collectionClass) {
		this.collectionClass = collectionClass;
	}	
	
	@SuppressWarnings("unchecked")
	public Class<? extends Collection> getCollectionClass() {
		return this.collectionClass;
	}
	
	public void setMaxSelection(Integer maxSelection) {
		this.maxSelection = maxSelection;
	}

	@SuppressWarnings("unchecked")
	protected void afterBindingSet() {
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
			else if (Collection.class.isAssignableFrom(type)) {
				collectionClass = (Class<Collection>) type;
			}
			else {
				collectionClass = ArrayList.class;
			}
		}
	}
	
	public final void setValue(Object value) {
		selectedValues = Generics.newArrayList();
		if (value != null) {
			Collection<?> collection = (Collection<?>) value;
			selectedValues.addAll(collection);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Object getValue() {
		Collection<Object> collection = null;
		if (getEditorBinding() != null) {
			collection = (Collection<Object>) getEditorBinding().getValue();
		}
		if (collection == null) {
			collection = SpringUtils.newInstance(collectionClass);
		}

		ArrayList<?> oldValues = Generics.newArrayList(collection);
		collection.clear();
		
		for (Object value : selectedValues) {
			int i = oldValues.indexOf(value);
			if (i >= 0) {
				value = oldValues.get(i);
			}
			collection.add(value);
		}
		return collection;
	}

	protected Collection<Object> getSelectedValues() {
		return selectedValues;
	}

	protected boolean hasSelection() {
		return !selectedValues.isEmpty();
	}
	
	public boolean isSelected(OptionItem option) {
		return selectedValues != null && 
				selectedValues.contains(option.getValue());
	}

	/**
	 * @see org.riotfamily.forms.AbstractElement#processRequest
	 */
	public void processRequest(FormRequest request) {
		updateSelection(request.getParameterValues(getParamName()));
	}
	
	protected void updateSelection(Collection<?> optionValues) {
		if (optionValues != null) {
			for (Object value : optionValues) {
				int i = selectedValues.indexOf(value);
				if (i >= 0) {
					selectedValues.remove(i);
					selectedValues.add(i, value);
				}
			}
		}
	}
	
	private void updateSelection(String[] indexes) {
		List<OptionItem> options = getOptionItems();
		selectedValues = Generics.newArrayList();
		if (indexes != null) {
			for (int i = 0; i < indexes.length; i++) {
				int index = Integer.parseInt(indexes[i]);
				if (index != -1) {
					selectedValues.add(((OptionItem) options.get(index)).getValue());
				}
			}
		}
		validate();
	}
	
	public void validate() {
		super.validate();
		if (maxSelection != null && selectedValues.size() > maxSelection.intValue()) {
			ErrorUtils.reject(this, "tooManyValuesSelected", maxSelection);
		}
	}
	
	public void handleJavaScriptEvent(JavaScriptEvent event) {
		if (event.getType() == JavaScriptEvent.ON_CHANGE) {
			Collection<Object> oldValue = selectedValues;
			updateSelection(event.getValues());
			fireChangeEvent(selectedValues, oldValue);
		}
	}

}
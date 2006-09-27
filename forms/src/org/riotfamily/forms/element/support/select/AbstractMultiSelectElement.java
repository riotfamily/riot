package org.riotfamily.forms.element.support.select;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.forms.ajax.JavaScriptEvent;
import org.riotfamily.forms.error.ErrorUtils;

/**
 * Abstract superclass for elements that let the user choose from a set of
 * options like selectboxes or radio button groups.
 */
public abstract class AbstractMultiSelectElement 
		extends AbstractSelectElement {

	private ArrayList selectedValues = new ArrayList();

	private Class collectionClass;

	private Integer maxSelection;
	
	/**
	 * Sets the class to use if a new collection instance needs to be created.
	 * If no class is set a suitable implementation will be selected according
	 * to the type of the property the element is bound to.
	 * 
	 * @param collectionClass the class to use for new collections
	 */
	public void setCollectionClass(Class collectionClass) {
		this.collectionClass = collectionClass;
	}	
	
	public void setMaxSelection(Integer maxSelection) {
		this.maxSelection = maxSelection;
	}

	protected void afterBindingSet() {
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
	}
	
	public final void setValue(Object value) {
		selectedValues = new ArrayList();
		if (value != null) {
			Collection collection = (Collection) value;
			selectedValues.addAll(collection);
		}
	}
	
	public Object getValue() {
		Collection collection = null;
		if (getEditorBinding() != null) {
			collection = (Collection) getEditorBinding().getValue();
		}
		if (collection == null) {
			try {
				collection = (Collection) collectionClass.newInstance();
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		else {
			collection.clear();
		}
		collection.addAll(selectedValues);
		return collection;
	}

	protected Collection getSelectedValues() {
		return selectedValues;
	}

	protected boolean hasSelection() {
		return !selectedValues.isEmpty();
	}
	
	public boolean isSelected(Option option) {
		return selectedValues != null && 
				selectedValues.contains(option.getObject());
	}

	/**
	 * @see org.riotfamily.forms.element.support.AbstractElement#processRequest
	 */
	public void processRequest(HttpServletRequest request) {
		updateSelection(request.getParameterValues(getParamName()));
	}
	
	private void updateSelection(String[] indexes) {
		List options = getOptions();
		selectedValues = new ArrayList();
		if (indexes != null) {
			for (int i = 0; i < indexes.length; i++) {
				int index = Integer.parseInt(indexes[i]);
				if (index != -1) {
					selectedValues.add(((Option) options.get(index)).getObject());
				}
			}
		}
		validate();
	}
	
	protected void validate() {
		super.validate();
		if (maxSelection != null && selectedValues.size() > maxSelection.intValue()) {
			ErrorUtils.reject(this, "tooManyValuesSelected", maxSelection);
		}
	}
	
	public void handleJavaScriptEvent(JavaScriptEvent event) {
		if (event.getType() == JavaScriptEvent.ON_CHANGE) {
			Collection oldValue = selectedValues;
			updateSelection(event.getValues());
			fireChangeEvent(selectedValues, oldValue);
		}
	}

}
package org.riotfamily.forms.element.support.select;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.forms.ajax.JavaScriptEvent;

/**
 * Abstract superclass for elements that let the user choose from a set of
 * options like selectboxes or radio button groups.
 */
public abstract class AbstractSingleSelectElement 
		extends AbstractSelectElement {
	
	private Object selectedValue;
	
	public final void setValue(Object value) {
		this.selectedValue = value;
	}
	
	public Object getValue() {
		return selectedValue;
	}

	protected boolean hasSelection() {
		return selectedValue != null;
	}
	
	public boolean isSelected(Option option) {
		return hasSelection() && selectedValue.equals(option.getObject());
	}

	/**
	 * @see org.riotfamily.forms.element.support.AbstractElement#processRequest
	 */
	public void processRequest(HttpServletRequest request) {
		updateSelection(request.getParameter(getParamName()));
	}
	
	private void updateSelection(String index) {
		int i = -1;
		if (index != null) {
			i = Integer.parseInt(index);
		}
		if (i >= 0) {
			Option option = (Option) getOptions().get(i);
			selectedValue = option.getObject();
		}
		else {
			selectedValue = null;	
		}
		validate();
	}
	
	public void handleJavaScriptEvent(JavaScriptEvent event) {
		if (event.getType() == JavaScriptEvent.ON_CHANGE) {
			Object oldValue = selectedValue;
			updateSelection(event.getValue());
			fireChangeEvent(selectedValue, oldValue);
		}
	}

}
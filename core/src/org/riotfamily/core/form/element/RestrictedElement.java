package org.riotfamily.core.form.element;

import org.riotfamily.core.security.AccessController;
import org.riotfamily.forms.element.AbstractConditionalElement;

/**
 * @since 6.5
 */
public class RestrictedElement extends AbstractConditionalElement {
	
	private boolean readOnly = true;

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	
	protected boolean isEditable() {
		if (readOnly) {
			return false;
		}
		return AccessController.isGranted(
				"edit-" + getEditor().getFieldName(), 
				getForm().getBackingObject());
	}
}

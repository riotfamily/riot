package org.riotfamily.forms.element;





public class EditableIfNew extends AbstractConditionalElement {
	
	protected boolean isEditable() {
		return getForm().isNew();
	}

}

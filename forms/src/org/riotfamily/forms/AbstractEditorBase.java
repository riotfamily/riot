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
package org.riotfamily.forms;


/**
 * Abstract base class for editor elements.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public abstract class AbstractEditorBase extends AbstractElement {

	private String paramName;

	private String desiredParamName;
	
	private EditorBinding binding;
	
	private String fieldName;
	
	public EditorBinding getEditorBinding() {
		return binding;
	}
	
	public final void setEditorBinding(EditorBinding binding) {
		this.binding = binding;
		afterBindingSet();
	}	
	
	protected void afterBindingSet() {
	}
	
	public void setDesiredParamName(String desiredParamName) {
		this.desiredParamName = desiredParamName;
	}
	
	protected String getDesiredParamName() {
		if (desiredParamName != null) {
			return desiredParamName;	
		}
		if (binding != null) {
			return binding.getProperty();
		}
		return null;
	}
	
	public String getParamName() {
		if (paramName == null) {
			paramName = getForm().createUniqueParameterName(getDesiredParamName());
			log.debug("Param name for element " + getId() + " is: " + paramName);
		}
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	public String getFieldName() {
		if (fieldName != null) {
			return fieldName;
		}
		if (binding != null) {
			return binding.getPropertyPath();
		}
		return "unbound-" + getId();
	}
	
	public String getLabel() {
		String label = super.getLabel();
		if (label == null && binding != null) {
			label = MessageUtils.getLabel(this, binding);
			super.setLabel(label);
		}		
		return label;
	}
		
	public String getHint() {
		String hint = super.getHint();
		if (hint == null && binding != null) {
			hint = MessageUtils.getHint(this, binding);
			super.setHint(hint);
		}		
		return hint;
	}
}

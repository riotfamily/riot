package org.riotfamily.forms.element.support;

import org.riotfamily.forms.bind.EditorBinding;
import org.riotfamily.forms.i18n.MessageUtils;

public abstract class AbstractEditorBase extends AbstractElement {

	private String paramName;

	private String desiredParamName;
	
	private EditorBinding binding;
	
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

	public String getFieldName() {
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

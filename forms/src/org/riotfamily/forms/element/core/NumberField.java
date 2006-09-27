package org.riotfamily.forms.element.core;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.riotfamily.forms.element.DHTMLElement;
import org.riotfamily.forms.error.ErrorUtils;
import org.riotfamily.forms.resource.ResourceElement;
import org.riotfamily.forms.resource.Resources;
import org.riotfamily.forms.resource.ScriptResource;
import org.riotfamily.forms.resource.ScriptSequence;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class NumberField extends TextField implements DHTMLElement, 
		ResourceElement {

	private static final List RESOURCES = Collections.singletonList(
			new ScriptSequence(new ScriptResource[] {
				Resources.PROTOTYPE, Resources.RIOT_NUMBER_INPUT 
			}));
	
	private Float minValue;
	
	private Float maxValue;
	
	private int precision = 2;
	
	private boolean allowFloats;
	
	private boolean spinner;
	
	private float stepSize = 1;
	
	private String unit;
	
	public NumberField() {
		setStyleClass("number");
	}	
	
	public void setMaxValue(Float maxValue) {
		this.maxValue = maxValue;
	}

	public void setMinValue(Float minValue) {
		this.minValue = minValue;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public void setSpinner(boolean spinner) {
		this.spinner = spinner;
	}

	public void setStepSize(float stepSize) {
		this.stepSize = stepSize;
	}	

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Collection getResources() {
		return RESOURCES;
	}

	public String getInitScript() {
		StringBuffer sb = new StringBuffer();
		sb.append("NumberInput.create('");
		sb.append(getId());
		sb.append("', {");
		sb.append("required:").append(isRequired()).append(',');
		appendValue(sb, "minValue", minValue);
		appendValue(sb, "maxValue", maxValue);
		if (allowFloats) {
			sb.append("allowFloats:true,");
			sb.append("precision:").append(precision).append(',');
		}
		if (unit != null) {
			sb.append("unit:'").append(unit).append("',");
		}
		if (spinner) {
			if (allowFloats) {
				sb.append("stepSize:").append(stepSize).append(',');
			}
			else {
				sb.append("stepSize:").append((int)stepSize).append(',');
			}
			sb.append("spinButtonTag:'div'");
		}
		else {
			sb.append("spinner:false");
		}
		sb.append("});");
		return sb.toString();
	}

	private static void appendValue(StringBuffer sb, String name, Float value) {
		sb.append(name);
		sb.append(':');
		if (value != null) {
			sb.append(value);
		}
		else {
			sb.append(false);
		}
		sb.append(',');
	}

	public void setValue(Object value) {			
		super.setValue(value);
	}
	
	protected void afterBindingSet() {
		Class type = getEditorBinding().getPropertyType();
		Assert.notNull(type, "Unable to determine type of property '" + 
				getEditorBinding().getProperty() + "'");		
		if (type.equals(Float.class) || type.equals(Double.class)
				|| type.equals(float.class)	|| type.equals(double.class)) {
			
			allowFloats = true;
		}		
		super.afterBindingSet();
	}
	
	protected void validate() {		
		super.validate();
		 if (getEditorBinding().getPropertyType().isPrimitive() 
				 && !StringUtils.hasLength(getText())) {	 
			ErrorUtils.reject(this, "required");						 
		 }		
	}
	
	

}

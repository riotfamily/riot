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
package org.riotfamily.forms.element;

import org.riotfamily.forms.DHTMLElement;
import org.riotfamily.forms.resource.FormResource;
import org.riotfamily.forms.resource.ResourceElement;
import org.riotfamily.forms.resource.Resources;
import org.riotfamily.forms.resource.ScriptResource;

public class NumberField extends TextField implements DHTMLElement,
		ResourceElement {

	private Float minValue;

	private Float maxValue;

	private Integer precision;

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

	public void setPrecision(Integer precision) {
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

	public FormResource getResource() {
		return new ScriptResource("riot-js/number-input.js", "NumberInput",
				Resources.PROTOTYPE);
	}

	public String getInitScript() {
		StringBuffer sb = new StringBuffer();
		sb.append("NumberInput.create('");
		sb.append(getId());
		sb.append("', {");
		sb.append("required:").append(isRequired()).append(',');
		appendValue(sb, "minValue", minValue);
		appendValue(sb, "maxValue", maxValue);
		if (precision != null && precision.intValue() > 0) {
			sb.append("allowFloats:true,");
			sb.append("precision:").append(precision).append(',');
		}
		if (unit != null) {
			sb.append("unit:'").append(unit).append("',");
		}
		if (spinner) {
			sb.append("stepSize:").append(stepSize).append(',');
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

	protected void afterBindingSet() {
		Class type = getEditorBinding().getPropertyType();
		if (precision == null && (type.equals(Float.class) 
				|| type.equals(Double.class)
				|| type.equals(float.class)	
				|| type.equals(double.class))) {
		
			precision = new Integer(2);
		}
		if (type.isPrimitive()) {
			setRequired(true);
		}
		super.afterBindingSet();
	}

}

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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.components.config;

import java.util.Map;

import org.riotfamily.common.beans.PropertyUtils;

public class ComponentConfig {

	private String type;
	
	private String label;
	
	private int min;
	
	private Integer max;
	
	private Map<String, ?> defaults;

	
	@SuppressWarnings("unchecked")
	public ComponentConfig(Object obj) {
		if (obj instanceof String) {
			type = (String) obj;
		}
		else if (obj instanceof Map) {
			Map map = (Map) obj;
			PropertyUtils.setProperties(this, map);
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLabel() {
		if (label == null) {
			label = type;
		}
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public Integer getMax() {
		return max;
	}

	public void setMax(Integer max) {
		this.max = max;
	}

	public Map<String, ?> getDefaults() {
		return defaults;
	}

	public void setDefaults(Map<String, ?> defaults) {
		this.defaults = defaults;
	}

}

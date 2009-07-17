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
package org.riotfamily.components.config;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.riotfamily.common.util.Generics;

public class ComponentListConfig {

	private Integer min;
	
	private Integer max;
	
	private Map<String, ComponentConfig> validTypes  = Generics.newLinkedHashMap();
	
	private List<String> initialTypes = Generics.newArrayList();

	public ComponentListConfig(Integer min, Integer max, 
			List<String> initial, List<?> valid) {
		
		this.min = min;
		this.max = max;
		if (initial != null) {
			initialTypes.addAll(initial); 
		}
		if (valid != null && !valid.isEmpty()) {
			for (Object obj : valid) {
				ComponentConfig config = new ComponentConfig(obj);
				validTypes.put(config.getType(), config);
				if (config.getMin() > 0) {
					int count = 0;
					for (String initialType : initialTypes) {
						if (initialType.equals(config.getType())) {
							count++;
						}
					}
					for (int i = count; i < config.getMin(); i++) {
						initialTypes.add(config.getType());
					}
				}
			}
		}
		else if (initial != null) {
			for (String initialType : initial) {
				if (!validTypes.containsKey(initialType)) {
					validTypes.put(initialType, new ComponentConfig(initialType));
				}
			}
		}
	}

	public Integer getMin() {
		return min;
	}

	public Integer getMax() {
		return max;
	}

	public Collection<ComponentConfig> getValidTypes() {
		return validTypes.values();
	}

	public List<String> getInitialTypes() {
		return initialTypes;
	}
	
	public ComponentConfig getConfig(String type) {
		return validTypes.get(type);
	}
	
	public String toJSON() {
		return JSONObject.fromObject(this).toString();
	}

}

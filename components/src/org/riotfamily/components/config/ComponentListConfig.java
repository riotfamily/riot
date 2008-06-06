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

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;

public class ComponentListConfig {

	private Integer minComponents;
	
	private Integer maxComponents;
	
	private List<String> validComponentTypes;
	
	private List<String> initialComponentTypes;

	
	public ComponentListConfig(Integer minComponents, Integer maxComponents,
			List<String> initialComponentTypes, 
			List<String> validComponentTypes) {

		this.minComponents = minComponents;
		this.maxComponents = maxComponents;
		this.initialComponentTypes = initialComponentTypes;
		this.validComponentTypes = validComponentTypes;
		if (validComponentTypes == null || validComponentTypes.isEmpty()) {
			Assert.notNull(initialComponentTypes, "Either inital or valid component types must be specified");
			this.validComponentTypes = new ArrayList<String>(initialComponentTypes);
		}
	}

	public Integer getMinComponents() {
		return minComponents;
	}

	public Integer getMaxComponents() {
		return maxComponents;
	}

	public List<String> getValidComponentTypes() {
		return validComponentTypes;
	}

	public List<String> getInitialComponentTypes() {
		return initialComponentTypes;
	}

}

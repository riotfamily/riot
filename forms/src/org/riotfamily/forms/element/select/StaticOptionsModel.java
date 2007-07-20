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
package org.riotfamily.forms.element.select;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.util.StringUtils;

public class StaticOptionsModel implements OptionsModel {

	private Collection optionValues;
	
	public StaticOptionsModel() {
	}

	public StaticOptionsModel(Collection options) {
		this.optionValues = options;
	}

	public void setOptionValues(Collection options) {
		this.optionValues = options;
	}
	
	public void setCommaDelimitedValues(String s) {
		optionValues = new ArrayList();
		String[] tokens = StringUtils.commaDelimitedListToStringArray(s);
		for (int i = 0; i < tokens.length; i++) {
			optionValues.add(tokens[i]);
		}
	}

	public Collection getOptionValues() {
		return optionValues;
	}
}

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
 * Portions created by the Initial Developer are Copyright (C) 2008
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.components.riot.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.riotfamily.components.model.wrapper.ValueWrapper;
import org.riotfamily.components.model.wrapper.ValueWrapperService;
import org.riotfamily.forms.options.OptionsModel;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class StaticContentOptionsModel implements OptionsModel {

	private Collection values;
		
	public void setValues(Collection values) {
		this.values = values;
	}

	public Collection getOptionValues() {
		ArrayList optionValues = new ArrayList();
		Iterator it = values.iterator();
		while (it.hasNext()) {
			ValueWrapper wrapper = ValueWrapperService.wrap(it.next());
			optionValues.add(wrapper);
		}
		return optionValues;
	}
	
	
	
}

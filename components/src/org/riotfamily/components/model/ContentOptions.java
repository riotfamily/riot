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
package org.riotfamily.components.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.riotfamily.components.model.wrapper.ListWrapper;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class ContentOptions {

	private String id;
	
	private ListWrapper listWrapper;

	
	public ContentOptions() {
	}
	
	public ContentOptions(String id, Collection values) {
		this.id = id;
		this.listWrapper = new ListWrapper();
		this.listWrapper.addAll(values);
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Collection getValues() {
		return listWrapper.getWrapperList();
	}
	
	public void update(Collection rawValues) {
		ListWrapper newValues = new ListWrapper();
		newValues.addAll(rawValues);
		ArrayList updated = new ArrayList(rawValues.size());
		Iterator it = newValues.iterator();
		while (it.hasNext()) {
			Object newValue = it.next();
			int i = listWrapper.indexOf(newValue);
			if (i == -1) {
				updated.add(newValue);
			}
			else {
				updated.add(listWrapper.get(i));
			}
		}
		listWrapper.clear();
		listWrapper.addAll(updated);
	}
	
}

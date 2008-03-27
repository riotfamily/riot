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
package org.riotfamily.components.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.springframework.util.Assert;


/**
 */
public class Component extends ContentContainer {

	private String type;
	
	private ComponentList liveList;

	private ComponentList previewList;

	private Set childLists;

	public Component() {
	}

	public Component(String type) {
		this.type = type;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		Assert.isTrue(this.type == null || this.type.equals(type) || getLiveVersion() == null, 
				"Can't change the type of a live Component");
		
		this.type = type;
	}

	public ComponentList getList() {
		return this.previewList != null ? previewList : liveList;
	}

	public void setList(ComponentList list) {
		this.liveList = list;
		this.previewList = list;
	}

	public Set getChildLists() {
		return this.childLists;
	}

	public void setChildLists(Set childLists) {
		this.childLists = childLists;
	}
	
	public Component createCopy() {
		Component copy = new Component(type);
		if (getLiveVersion() != null) {
			copy.setLiveVersion(new Content(getLiveVersion()));
		}
		if (getPreviewVersion() != null) {
			copy.setPreviewVersion(new Content(getPreviewVersion()));
		}
		return copy;
	}
	
	public Component createCopy(String path) {
		Component copy = createCopy();
		if (childLists != null) {
			HashSet clonedLists = new HashSet();
			Iterator it = childLists.iterator();
			while (it.hasNext()) {
				ComponentList list = (ComponentList) it.next();
				ComponentList clonedList = list.createCopy(null);
				clonedList.setParent(copy);
				clonedLists.add(clonedList);
			}
			copy.setChildLists(clonedLists);
		}
		return copy;
	}

}

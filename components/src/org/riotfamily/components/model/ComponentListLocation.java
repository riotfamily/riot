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
package org.riotfamily.components.model;

import org.riotfamily.components.locator.ComponentListLocator;

/**
 * Class that represents the location of a {@link ComponentList}.
 * @see ComponentListLocator
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class ComponentListLocation {

	private String type;

	private String path;

	private String slot;

	public ComponentListLocation() {
	}

	public ComponentListLocation(ComponentListLocation location) {
		type = location.getType();
		path = location.getPath();
		slot = location.getSlot();
	}

	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getSlot() {
		return this.slot;
	}

	public void setSlot(String slot) {
		this.slot = slot;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean equals(Object obj) {
		if (obj instanceof ComponentListLocation) {
			return toString().equals(obj.toString());
		}
		return false;
	}

	public int hashCode() {
		return toString().hashCode();
	}

	public String toString() {
		return type + "://" + path + '#' + slot;
	}

}

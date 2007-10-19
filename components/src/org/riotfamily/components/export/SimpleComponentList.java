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
 *   "Felix Gnass [fgnass at neteye dot de]"
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.components.export;

import java.util.List;

import org.riotfamily.components.model.Location;

/**
 * Class that holds a list of SimpleComponent instances. Unlike a ComponentList
 * this class does not provide any version information. Purpose of this class
 * is to simplify the object model in order to facilitate export or conversion
 * tasks. 
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class SimpleComponentList {

	private Location location;
	
	private List components;

	public Location getLocation() {
		return this.location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public List getComponents() {
		return this.components;
	}

	public void setComponents(List components) {
		this.components = components;
	}

}

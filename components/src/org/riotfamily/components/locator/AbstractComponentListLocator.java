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
package org.riotfamily.components.locator;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.components.model.Location;

/**
 * Abstract base-class that delegates the resolution of the
 * {@link Location#getSlot() location's slot property} to a {@link SlotResolver}.
 *
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public abstract class AbstractComponentListLocator
		implements ComponentListLocator {

	private String type;

	private SlotResolver slotResolver = new DefaultSlotResolver();

	public AbstractComponentListLocator(String type) {
		this.type = type;
	}

	public void setSlotResolver(SlotResolver slotResolver) {
		this.slotResolver = slotResolver;
	}

	public boolean supports(String type) {
		return this.type.equals(type);
	}

	public Location getLocation(HttpServletRequest request) {
		Location location = new Location();
		location.setType(getType(request));
		location.setPath(getPath(request));
		location.setSlot(slotResolver.getSlot(request));
		return location;
	}

	protected String getType(HttpServletRequest request) {
		return type;
	}

	protected abstract String getPath(HttpServletRequest request);

	public Location getParentLocation(Location location) {
		Location parent = new Location();
		parent.setType(type);
		parent.setPath(getParentPath(location.getPath()));
		parent.setSlot(location.getSlot());
		return parent;
	}

	protected abstract String getParentPath(String path);

	public String getUrl(Location location) {
		return getUrlForPath(location.getPath());
	}

	protected abstract String getUrlForPath(String path);

}

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
package org.riotfamily.components;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.components.render.InheritingRenderStrategy;

/**
 * Interface that returns the {@link Location} of the ComponentList that should
 * be rendered.
 *
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public interface ComponentListLocator {

	/**
	 * Returns the location type associated with the locator.
	 * @see Location#getType()
	 */
	public String getType();

	/**
	 * Returns the {@link Location} for the given request.
	 */
	public Location getLocation(HttpServletRequest request);

	/**
	 * Returns the location that should be used to display inherited components.
	 * @see InheritingRenderStrategy
	 */
	public Location getParentLocation(Location location);

	/**
	 * Returns the URL under which the given location can be seen.
	 */
	public String getUrl(Location location);

}

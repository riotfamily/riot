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
package org.riotfamily.components;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;




public interface Component {

	/**
	 * Indicates whether the content rendered by the component depends on
	 * anything other but the components internal data.
	 */
	public boolean isDynamic();
	
	public void addPropertyProcessor(PropertyProcessor propertyProcessor);
	
	public Map buildModel(ComponentVersion version);
	
	public void updateProperties(ComponentVersion version, Map model);	
	
	public List getPropertyProcessors();	
	
	/**
	 * Renders the given ComponentVersion.
	 */
	public void render(ComponentVersion version, String positionClassName, 
			HttpServletRequest request, HttpServletResponse response) 
			throws IOException;
	
	/**
	 * Returns a Collection of Strings that should be used to tag the
	 * CacheItem containing the rendered component.
	 */
	public Collection getCacheTags(ComponentVersion version);
	
}

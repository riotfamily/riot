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
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.component.property;

import java.util.Map;

public interface PropertyProcessor {

	/**
	 * Replaces strings in the map by objects suitable for rendering or
	 * editing. The method is invoked before a component is rendered or edited
	 * using a form.
	 */
	public void resolveStrings(Map map);
	
	/**
	 * Replaces objects in the map by their string representation. The method
	 * is invoked after a component model has been edited using a form and
	 * before it is persisted. 
	 */
	public void convertToStrings(Map map);
	
	/**
	 * Copies strings from one map to another. The method is invoked when a
	 * copy of a component model needs to be created. Implementors can use this
	 * hook to clone referenced objects. See {@link FileStoreProperyProcessor}
	 * for an example.
	 */
	public void copy(Map source, Map dest);
	
	/**
	 * Deletes orphaned resources. The method is invoked when a component model
	 * is deleted. Implementors can use this hook to delete referenced objects
	 * or resources. See {@link FileStoreProperyProcessor}
	 * for an example.
	 */
	public void delete(Map map);
	
	/**
	 * Implementors may return an array of Strings that are used to tag the 
	 * CacheItem that contains the rendered component markup.
	 */
	public String[] getCacheTags(Map map);
	
}

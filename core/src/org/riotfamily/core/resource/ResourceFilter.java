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
package org.riotfamily.core.resource;

import java.io.FilterReader;
import java.io.Reader;

import javax.servlet.http.HttpServletRequest;

/**
 * Interface that allows to filter resources that are served by a 
 * ResourceController.
 * 
 * @see org.riotfamily.core.resource.ResourceController#setFilters
 */
public interface ResourceFilter {

	/**
	 * Returns whether the filter should be applied to the resource denoted
	 * by the given path.
	 */
	public boolean matches(String path);
	
	/**
	 * Returns a FilterReader that does the actual filtering.
	 */
	public FilterReader createFilterReader(Reader in, HttpServletRequest request);

}

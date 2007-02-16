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
package org.riotfamily.common.web.mapping;

import java.util.Map;

import org.springframework.web.servlet.HandlerMapping;

/**
 * Interface that can be implemented by {@link HandlerMapping HandlerMappings} 
 * in order to allow reverse lookups. This way you can obtain an URL for a 
 * mapped handler.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public interface UrlMapping {

	/**
	 * Returns the URL of a mapped handler.
	 * @param beanName The name of the handler
	 * @param attributes Map of request attributes to be incorporated into the 
	 * 	   URL. Only relevant for HandlerMappings that export parts of the URL
	 *     as request attributes.
	 */
	public String getUrl(String beanName, Map attributes);

}

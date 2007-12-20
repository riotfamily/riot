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
import java.util.Map;

/**
 * Simple component model without version information. In contrast to a
 * ComponentVersion which only holds String vales, this class provides a map 
 * containing resolved/converted properties.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class SimpleComponent {

	private String type;
	
	private Map properties;
	
	private List childLists;

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Map getProperties() {
		return this.properties;
	}

	public void setProperties(Map properties) {
		this.properties = properties;
	}

	public List getChildLists() {
		return this.childLists;
	}

	public void setChildLists(List childLists) {
		this.childLists = childLists;
	}
	
}

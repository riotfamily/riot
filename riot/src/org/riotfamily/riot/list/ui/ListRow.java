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
package org.riotfamily.riot.list.ui;

import java.util.Collection;

/**
 * Holds information about a list row.
 */
public class ListRow {

	private String cssClass;
	
	private String objectId;

	private Collection cells;

	public ListRow(String cssClass, String objectId, Collection cells) {
		this.cssClass = cssClass;
		this.objectId = objectId;
		this.cells = cells;
	}

	public Collection getCells() {
		return cells;
	}

	public String getObjectId() {
		return objectId;
	}

	public String getCssClass() {
		return this.cssClass;
	}
	
}

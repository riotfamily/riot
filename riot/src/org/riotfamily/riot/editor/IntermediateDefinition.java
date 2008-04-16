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
package org.riotfamily.riot.editor;


public class IntermediateDefinition extends AbstractObjectEditorDefinition {

	private static final String TYPE_NONE = "none";
	
	private ListDefinition nestedListDefinition;

	public IntermediateDefinition(ListDefinition parentListDefinition,
			ListDefinition nestedListDefinition) {

		setParentEditorDefinition(parentListDefinition);
		this.nestedListDefinition = nestedListDefinition;
		nestedListDefinition.setParentEditorDefinition(this);
		setEditorRepository(parentListDefinition.getEditorRepository());
	}

	public String getEditorType() {
		return TYPE_NONE;
	}
	
	public ListDefinition getNestedListDefinition() {
		return this.nestedListDefinition;
	}

	protected String getEditorUrlWithinServlet(String objectId, String parentId,
			String parentEditorId) {
		
		return null;
	}
}

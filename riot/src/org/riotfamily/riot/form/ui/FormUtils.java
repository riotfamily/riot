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
package org.riotfamily.riot.form.ui;

import org.riotfamily.forms.Form;
import org.riotfamily.riot.editor.ObjectEditorDefinition;
import org.riotfamily.riot.editor.EditorConstants;

public class FormUtils {

	public static void setObjectId(Form form, String objectId) {
		form.setAttribute(EditorConstants.OBJECT_ID, objectId);
	}

	public static String getObjectId(Form form) {
		return (String) form.getAttribute(EditorConstants.OBJECT_ID);
	}

	public static void setParentId(Form form, String objectId) {
		form.setAttribute(EditorConstants.PARENT_ID, objectId);
	}

	public static String getParentId(Form form) {
		return (String) form.getAttribute(EditorConstants.PARENT_ID);
	}

	public static void setEditorDefinition(Form form,
			ObjectEditorDefinition editorDefinition) {

		form.setAttribute("editorDefinition", editorDefinition);
	}

	public static ObjectEditorDefinition getEditorDefinition(Form form) {
		return (ObjectEditorDefinition) form.getAttribute("editorDefinition");
	}

}

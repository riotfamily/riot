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
package org.riotfamily.riot.form.ui;

import org.riotfamily.forms.Form;

public class FormUtils {

	private static final String OBJECT_ID_ATTRIBUTE = "objectId";
	
	private static final String PARENT_ID_ATTRIBUTE = "parentId";
	
	public static void setObjectId(Form form, String objectId) {
		form.setAttribute(OBJECT_ID_ATTRIBUTE, objectId);
	}
	
	public static String getObjectId(Form form) {
		return (String) form.getAttribute(OBJECT_ID_ATTRIBUTE);
	}
	
	public static void setParentId(Form form, String objectId) {
		form.setAttribute(PARENT_ID_ATTRIBUTE, objectId);
	}
	
	public static String getParentId(Form form) {
		return (String) form.getAttribute(PARENT_ID_ATTRIBUTE);
	}
	
}

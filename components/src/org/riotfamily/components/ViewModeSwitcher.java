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

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class ViewModeSwitcher {

	private static final String SESSION_ATTRIBUTE = 
			ViewModeSwitcher.class.getName() + ".editMode";
	
	public static boolean isEditMode(HttpServletRequest request) {
		return request.getSession().getAttribute(SESSION_ATTRIBUTE) != Boolean.FALSE; 
	}
	
	public static void switchToEditMode(HttpServletRequest request) {
		request.getSession().setAttribute(SESSION_ATTRIBUTE, Boolean.TRUE);
	}
	
	public static void leaveEditMode(HttpServletRequest request) {
		request.getSession().setAttribute(SESSION_ATTRIBUTE, Boolean.FALSE);
	}
}

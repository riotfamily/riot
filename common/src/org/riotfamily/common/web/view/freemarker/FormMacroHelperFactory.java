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
package org.riotfamily.common.web.view.freemarker;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.view.MacroHelperFactory;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 8.0 (backported)
 */
public class FormMacroHelperFactory implements MacroHelperFactory {

	private static final String INSTANCE_ATTRIBUTE = FormMacroHelper.class.getName();

	/**
	 * Creates a {@link FormMacroHelper}. The helper is stored as request 
	 * attribute so that components can access the same instance. 
	 */
	public Object createMacroHelper(HttpServletRequest request,
			HttpServletResponse response) {
		
		FormMacroHelper helper = (FormMacroHelper) request.getAttribute(INSTANCE_ATTRIBUTE);
		if (helper == null) {
			helper = new FormMacroHelper();
			request.setAttribute(INSTANCE_ATTRIBUTE, helper);
		}
		return helper;
	}

}

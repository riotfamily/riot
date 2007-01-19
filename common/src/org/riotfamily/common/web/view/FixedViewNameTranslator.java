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
package org.riotfamily.common.web.view;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.RequestToViewNameTranslator;

/**
 * RequestToViewNameTranslator that returns always the same viewName.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class FixedViewNameTranslator implements RequestToViewNameTranslator {

	public static final String DEFAULT_VIEW_NAME = "NO_VIEW_NAME";
	
	private String viewName = DEFAULT_VIEW_NAME;
	
	/**
	 * Sets the viewName to return. Default is <code>"NO_VIEW_NAME"</code>. 
	 */
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	/**
	 * Returns the viewName set via {@link #setViewName(String)}.
	 */
	public String getViewName(HttpServletRequest request) throws Exception {
		return viewName;
	}

}

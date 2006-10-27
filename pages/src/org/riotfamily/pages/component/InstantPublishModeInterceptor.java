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
package org.riotfamily.pages.component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.pages.component.editor.ComponentEditor;
import org.riotfamily.pages.page.Page;
import org.riotfamily.pages.page.support.PageUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class InstantPublishModeInterceptor extends HandlerInterceptorAdapter {

	public boolean preHandle(HttpServletRequest request, 
			HttpServletResponse response, Object handler) throws Exception {
		
		Page page = PageUtils.getPage(request);
		if (page != null) {
			request.setAttribute(ComponentEditor.INSTANT_PUBLISH_ATTRIBUTE,
					Boolean.valueOf(page.isNew()));
		}
		return true;
	}

}

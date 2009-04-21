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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.riotfamily.common.util.Generics;
import org.riotfamily.common.web.util.ServletUtils;

public class FlashScope {

	private static final String SESSION_ATTRIBUTE = FlashScope.class.getName();

	private Map<String, Map<String, ?>> models = Generics.newHashMap();
		
	@SuppressWarnings("unchecked")
	public static void store(HttpServletRequest request, Map<?, ?> model, String url) {
		FlashScope flashScope = getFlashScope(request, true);
		flashScope.models.put(url, (Map<String, ?>) model);
	}
	
	public static void expose(HttpServletRequest request) {
		FlashScope flashScope = getFlashScope(request, false);
		if (flashScope != null) {
			String url = ServletUtils.getRequestUrlWithQueryString(request);
			Map<String, ?> model = flashScope.models.remove(url);
			if (model != null) {
				for (Map.Entry<String, ?> entry : model.entrySet()) {
					request.setAttribute(entry.getKey(), entry.getValue());
				}
			}
		}
	}

	private static FlashScope getFlashScope(HttpServletRequest request, boolean create) {
		FlashScope flashScope = null;
		HttpSession session = request.getSession(create);
		if (session != null) {
			flashScope = (FlashScope) session.getAttribute(SESSION_ATTRIBUTE);
			if (flashScope == null && create) {
				flashScope = new FlashScope();
				session.setAttribute(SESSION_ATTRIBUTE, flashScope);
			}
		}
		return flashScope;
	}

	
	
}
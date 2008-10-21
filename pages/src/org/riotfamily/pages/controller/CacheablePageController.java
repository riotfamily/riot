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
package org.riotfamily.pages.controller;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.cachius.spring.CacheableController;

public class CacheablePageController extends PageController 
		implements CacheableController {

	public String getCacheKey(HttpServletRequest request) {
		return request.getRequestURL().toString();
	}

	public long getLastModified(HttpServletRequest request) throws Exception {
		return System.currentTimeMillis();
	}

	public long getTimeToLive() {
		return CACHE_ETERNALLY;
	}

}

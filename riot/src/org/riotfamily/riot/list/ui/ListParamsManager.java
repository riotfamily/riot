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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.riot.list.ListConfig;
import org.riotfamily.riot.list.support.ListParamsImpl;
import org.springframework.web.util.WebUtils;

/**
 * Provides ListParams for the given request.
 */
public class ListParamsManager {

	private static final String SESSION_KEY = ListParamsManager.class.getName();
	
	public MutableListParams getListParams(ListConfig listConfig, 
			HttpServletRequest request) {
		
		Map map = getParamsMap(request);
		MutableListParams params = (MutableListParams) map.get(listConfig.getId());
		if (params == null) {
			params = new ListParamsImpl();
			map.put(listConfig.getId(), params);
		}
		return params;
	}

	protected Map getParamsMap(HttpServletRequest request) {
		return (Map) WebUtils.getOrCreateSessionAttribute(
				request.getSession(), SESSION_KEY, HashMap.class); 
	}

}

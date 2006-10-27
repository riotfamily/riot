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
package org.riotfamily.pages.component.resolver;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.web.util.ServletMappingHelper;
import org.riotfamily.pages.page.Page;
import org.riotfamily.pages.page.support.PageUtils;
import org.riotfamily.pages.setup.WebsiteConfigSupport;

public class PageComponentPathResolver extends WebsiteConfigSupport 
		implements ComponentPathResolver {

	private static ServletMappingHelper servletMappingHelper = 
			new ServletMappingHelper(true);
	
	public String getComponentPath(HttpServletRequest request) {
		return servletMappingHelper.getLookupPathForRequest(request);
	}
	
	public String getParentPath(String path) {
		Page page = getPageMap().getPage(path);
		Page parent = page.getParent();
		if (parent != null) {
			if (parent.isFolder()) {
				Page indexPage = PageUtils.getFirstChild(parent);
				if (page.equals(indexPage)) {
					return getParentPath(parent.getPath());
				}
				else {
					parent = indexPage;
				}
			}
			return parent.getPath();
		}
		return null;
	}

}

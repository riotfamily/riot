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
 *   Carsten Woelk [cwoelk at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.riotfamily.components.EditModeUtils;
import org.riotfamily.pages.dao.PageDao;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;

/**
 * @author Carsten Woelk [cwoelk at neteye dot de]
 * @since 6.6
 */
public class PagesMacroHelper {

	private PageDao pageDao;
	
	
	public PagesMacroHelper(PageDao pageDao) {
		this.pageDao = pageDao;
	}
	
	
	public List getTopLevelPages(Site site) {
		return getVisiblePages(pageDao.getRootNode().getChildPages(site),
				EditModeUtils.isEditMode());
	}

	public Page getPageForHandler(String handlerName, Site site) {
		return pageDao.findPageForHandler(handlerName, site);
	}

	public List getPagesForHandler(String handlerName, Site site) {
		return pageDao.findPagesForHandler(handlerName, site);
	}

	private List getVisiblePages(List pages, boolean preview) {
		ArrayList result = new ArrayList();
		Iterator it = pages.iterator();
		while (it.hasNext()) {
			Page page = (Page) it.next();
			if (page.isVisible(preview)) {
				result.add(page);
			}
		}
		return result;
	}

}

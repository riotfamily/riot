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
package org.riotfamily.pages.page.support;

import java.util.Iterator;
import java.util.List;

import org.riotfamily.pages.page.PageDao;
import org.riotfamily.pages.page.PersistentPage;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public class SitemapSetupBean implements InitializingBean {

	private PageDao pageDao;
	
	private List pages;
	
	public void setPageDao(PageDao pageDao) {
		this.pageDao = pageDao;
	}

	public void setPages(List pages) {
		this.pages = pages;
	}
	
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(pageDao, "A PageDao must be set.");
		List rootPages = pageDao.listRootPages();
		if (rootPages == null || rootPages.isEmpty()) {
			savePages();
		}
	}
	
	protected void savePages() {
		if (pages != null) {
			int position = 0;
			Iterator it = pages.iterator();
			while (it.hasNext()) {
				PersistentPage page = (PersistentPage) it.next();
				page.setPosition(position++);
				pageDao.savePage(page);
			}
		}
	}
}

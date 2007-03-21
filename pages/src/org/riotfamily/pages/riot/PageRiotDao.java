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
package org.riotfamily.pages.riot;

import java.util.Collection;
import java.util.Locale;

import org.riotfamily.pages.Page;
import org.riotfamily.pages.dao.PageDao;
import org.riotfamily.riot.dao.ListParams;
import org.riotfamily.riot.dao.ParentChildDao;
import org.springframework.dao.DataAccessException;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class PageRiotDao implements ParentChildDao {

	private PageDao pageDao;
	
	public PageRiotDao(PageDao pageDao) {
		this.pageDao = pageDao;
	}

	public Object getParent(Object entity) {
		Page page = (Page) entity;
		Page parentPage = page.getParentPage();
		if (parentPage != null) {
			return parentPage;
		}
		return page.getLocale();
	}

	public void delete(Object entity, Object parent) throws DataAccessException {
		Page page = (Page) entity;
		pageDao.deletePage(page);
	}

	public Class getEntityClass() {
		return Page.class;
	}

	public int getListSize(Object parent, ListParams params) 
			throws DataAccessException {

		return -1;
	}

	public String getObjectId(Object entity) {
		Page page = (Page) entity;
		return page.getId().toString();
	}

	public Collection list(Object parent, ListParams params) 
			throws DataAccessException {
		
		if (parent instanceof Page) {
			Page parentPage = (Page) parent;
			return parentPage.getChildPages(true);
		}
		else {
			Locale locale = (Locale) parent;
			return pageDao.listRootPages(locale);
		}
	}

	public Object load(String id) throws DataAccessException {
		return pageDao.loadPage(new Long(id));
	}

	public void save(Object entity, Object parent) throws DataAccessException {
		Page page = (Page) entity;
		if (parent instanceof Page) {
			Page parentPage = (Page) parent;
			pageDao.savePage(parentPage, page);
		}
		else {
			Locale locale = (Locale) parent;
			pageDao.saveRootPage(page, locale);
		}
	}

	public void update(Object entity) throws DataAccessException {
		pageDao.updatePage((Page) entity);
	}

}

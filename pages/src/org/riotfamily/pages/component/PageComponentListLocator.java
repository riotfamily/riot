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
package org.riotfamily.pages.component;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.components.locator.AbstractComponentListLocator;
import org.riotfamily.pages.Page;
import org.riotfamily.pages.PageLocation;
import org.riotfamily.pages.dao.PageDao;
import org.riotfamily.pages.mapping.PageLocationResolver;

/**
 * ComponentListLocator that uses the page-id as component-path.
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class PageComponentListLocator extends AbstractComponentListLocator {

	public static final String TYPE_PAGE = "page";
	
	private PageDao pageDao;
	
	private PageLocationResolver resolver;
	
	
	public PageComponentListLocator(PageDao pageDao, 
			PageLocationResolver resolver) {
		
		super(TYPE_PAGE);
		this.pageDao = pageDao;
		this.resolver = resolver;
	}

	protected String getPath(HttpServletRequest request) {
		Page page = (Page) request.getAttribute("page");
		return getPath(page);
	}

	protected String getUrlForPath(String path) {
		Page page = pageDao.loadPage(new Long(path));
		return resolver.getUrl(new PageLocation(page));
	}
	
	protected String getParentPath(String path) {
		Page page = pageDao.loadPage(new Long(path));
		Page parent = page.getParentPage();
		return parent != null ? getPath(parent) : null; 
	}

	public static String getPath(Page page) {
		return page != null ? page.getId().toString() : null;
	}

}

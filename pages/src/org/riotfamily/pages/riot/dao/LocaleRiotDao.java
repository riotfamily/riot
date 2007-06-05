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
package org.riotfamily.pages.riot.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.riotfamily.pages.Site;
import org.riotfamily.pages.dao.PageDao;
import org.riotfamily.riot.dao.ListParams;
import org.riotfamily.riot.dao.ParentChildDao;
import org.riotfamily.riot.dao.support.RiotDaoAdapter;
import org.springframework.dao.DataAccessException;
import org.springframework.util.StringUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class LocaleRiotDao extends RiotDaoAdapter
		implements ParentChildDao {

	private static final String ID_SEPARATOR = ",";

	private PageDao pageDao;

	public LocaleRiotDao(PageDao pageDao) {
		this.pageDao = pageDao;
	}

	public Class getEntityClass() {
		return SiteLocale.class;
	}

	public Collection list(Object parent, ListParams params) throws DataAccessException {
		Site site = (Site) parent;
		if (site == null) {
			site = pageDao.getDefaultSite();
		}
		List locales = pageDao.getLocales();
		ArrayList result = new ArrayList(locales.size());
		Iterator it = locales.iterator();
		while (it.hasNext()) {
			Locale locale = (Locale) it.next();
			result.add(new SiteLocale(site, locale));
		}
		return result;
	}

	public String getObjectId(Object entity) {
		SiteLocale siteLocale = (SiteLocale) entity;
		return siteLocale.getSite().getId().toString()
				+ ID_SEPARATOR + siteLocale.getLocale().toString();
	}

	public Object load(String id) throws DataAccessException {
		String[] s = StringUtils.split(id, ID_SEPARATOR);
		Site site = pageDao.loadSite(new Long(s[0]));
		Locale locale = StringUtils.parseLocaleString(s[1]);
		return new SiteLocale(site, locale);
	}

	public Object getParent(Object entity) {
		SiteLocale siteLocale = (SiteLocale) entity;
		return siteLocale.getSite();
	}
}

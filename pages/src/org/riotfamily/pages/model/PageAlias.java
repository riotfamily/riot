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
package org.riotfamily.pages.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.criterion.Restrictions;
import org.riotfamily.common.hibernate.ActiveRecordSupport;



/**
 * Alias for a page. Aliases are created whenever a page (or one of it's 
 * ancestors) is renamed or moved.
 *
 * @author Felix Gnass [fgnass at neteye dot de]
 * @author Jan-Frederic Linde [jfl at neteye dot de]
 * @since 6.5
 */
@Entity
@Table(name="riot_page_aliases")
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="pages")
public class PageAlias extends ActiveRecordSupport {


	private Page page;
	
	private Site site;
	
	private String path;
	
	public PageAlias() {
	}
	
	public PageAlias(Page page, Site site, String path) {
		this.page = page;
		this.site = site;
		this.path = path;
	}

	@ManyToOne
	public Page getPage() {
		return this.page;
	}

	public void setPage(Page page) {
		this.page = page;
	}
	
	@ManyToOne
	public Site getSite() {
		return this.site;
	}

	public void setSite(Site site) {
		this.site = site;
	}

	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String toString() {
		return "PageAlias[" + page + " --> " + path + "]";
	}
	
	// ----------------------------------------------------------------------
	// 
	// ----------------------------------------------------------------------
	
	public static PageAlias loadBySiteAndPath(Site site, String path) {
		return (PageAlias) getSession().createCriteria(PageAlias.class)
				.setCacheable(true)
				.setCacheRegion("pages")
				.add(Restrictions.eq("site", site))
				.add(Restrictions.eq("path", path))
				.uniqueResult();
	}
	
}

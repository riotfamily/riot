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
package org.riotfamily.pages.cache;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.cachius.Cache;
import org.riotfamily.cachius.TaggingContext;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public final class PageCacheUtils {

	private static final String PAGE_PREFIX = Page.class.getName() + '#';
	
	private static final String TOP_LEVEL_PAGES_PREFIX = Site.class.getName() 
			+ ":topLevelPages#";
	
	private static final String CHILD_PAGE_PREFIX = Page.class.getName() 
			+ ":childPages#";
	
	private PageCacheUtils() {
	}
	
	/**
	 * Returns the tag for the specified page. The generated tag will be
	 * "<code>org.riotfamily.pages.model.Page#<i>&lt;id&gt;</i></code>",
	 * where <i>&lt;id&gt;</i> is the id of the given page.
	 */
	public static String getPageTag(Page page) {
		return PAGE_PREFIX + page.getId();
	}
	
	/**
	 * Returns the tag for the top-level pages of the specified site. 
	 * The generated tag will be 
	 * "<code>org.riotfamily.pages.model.Site:topLevelPages#&lt;id&gt;</code>",
	 * where <i>&lt;id&gt;</i> is the id of the given site.
	 */
	public static String getTopLevelPagesTag(Site site) {
		return TOP_LEVEL_PAGES_PREFIX + site.getId();
	}

	/**
	 * Returns the tag for the child pages of the specified page. 
	 * The generated tag will be 
	 * "<code>org.riotfamily.pages.model.Page:childPages#&lt;id&gt;</code>",
	 * where <i>&lt;id&gt;</i> is the id of the given parent page.
	 */
	public static String getChildPagesTag(Page page) {
		return CHILD_PAGE_PREFIX + page.getId();
	}
	
	/**
	 * Returns the tag for the siblings of the specified page. The call is 
	 * delegated to {@link #getChildPagesTag(Page) 
	 * getChildPagesTag(page.getParentPage())}, or 
	 * {@link #getTopLevelPagesTag(Site) getTopLevelPagesTag(page.getSite())}
	 * in case the page has no parent.
	 */
	public static String getSiblingsTag(Page page) {
		if (page.getParentPage() == null) {
			return getTopLevelPagesTag(page.getSite());
		}
		return getChildPagesTag(page.getParentPage());
	}
	
	/**
	 * Adds the {@link #getTopLevelPagesTag(Site) top-level-pages} tag for the
	 * given site to the specified TaggingContext.
	 */
	public static void addTopLevelPagesTag(TaggingContext context, Site site) {
		if (context != null) {
			context.addTag(getTopLevelPagesTag(site));
		}
	}
	
	/**
	 * Adds the {@link #getTopLevelPagesTag(Site) top-level-pages} tag for the
	 * given site to the current TaggingContext of the specified request.
	 * @see TaggingContext#tag(HttpServletRequest, String)
	 */
	public static void addTopLevelPagesTag(HttpServletRequest request, Site site) {
		TaggingContext.tag(request, getTopLevelPagesTag(site));
	}
	
	/**
	 * Adds the {@link #getTopLevelPagesTag(Site) top-level-pages} tag for the
	 * given site to the current TaggingContext.
	 * @see TaggingContext#tag(String)
	 */
	public static void addTopLevelPagesTag(Site site) {
		TaggingContext.tag(getTopLevelPagesTag(site));
	}
	
	/**
	 * Adds the {@link #getPageTag(Site) page} tag for the given page to the 
	 * specified TaggingContext.
	 */
	public static void addPageTag(TaggingContext context, Page page) {
		if (context != null) {
			context.addTag(getPageTag(page));
		}
	}
	
	/**
	 * Adds the {@link #getPageTag(Site) page} tag for the given page to the 
	 * current TaggingContext of the specified request.
	 * @see TaggingContext#tag(HttpServletRequest, String)
	 */
	public static void addPageTag(HttpServletRequest request, Page page) {
		TaggingContext.tag(request, getPageTag(page));
	}
	
	/**
	 * Adds the {@link #getPageTag(Site) page} tag for the given page to the 
	 * current TaggingContext.
	 * @see TaggingContext#tag(String)
	 */
	public static void addPageTag(Page page) {
		TaggingContext.tag(getPageTag(page));
	}
	
	/**
	 * Adds the {@link #getChildPagesTag(Site) child-pages} tag for the 
	 * given parent page to the specified TaggingContext.
	 */
	public static void addChildPagesTag(TaggingContext context, Page page) {
		if (context != null) {
			context.addTag(getChildPagesTag(page));
		}
	}
	
	/**
	 * Adds the {@link #getChildPagesTag(Site) child-pages} tag for the 
	 * given parent page to the TaggingContext of the specified request.
	 * @see TaggingContext#tag(HttpServletRequest, String)
	 */
	public static void addChildPagesTag(HttpServletRequest request, Page page) {
		TaggingContext.tag(request, getChildPagesTag(page));
	}
	
	/**
	 * Adds the {@link #getChildPagesTag(Site) child-pages} tag for the 
	 * given parent page to the current TaggingContext.
	 * @see TaggingContext#tag(String)
	 */
	public static void addChildPagesTag(Page page) {
		TaggingContext.tag(getChildPagesTag(page));
	}
	
	/**
	 * Adds the {@link #getSiblingsTag(Site) siblings} tag for the 
	 * given page to the specified TaggingContext.
	 */
	public static void addSiblingsTag(TaggingContext context, Page page) {
		if (context != null) {
			context.addTag(getSiblingsTag(page));
		}
	}
	
	/**
	 * Adds the {@link #getSiblingsTag(Site) siblings} tag for the 
	 * given page to the TaggingContext of the specified request.
	 * @see TaggingContext#tag(HttpServletRequest, String)
	 */
	public static void addSiblingsTag(HttpServletRequest request, Page page) {
		TaggingContext.tag(request, getSiblingsTag(page));
	}
	
	/**
	 * Adds the {@link #getSiblingsTag(Site) siblings} tag for the 
	 * given page to the current TaggingContext.
	 * @see TaggingContext#tag(String)
	 */
	public static void addSiblingsTag(Page page) {
		TaggingContext.tag(getSiblingsTag(page));
	}
	
	public static void invalidateSiblings(Cache cache, Page page) {
		if (cache != null) {
			cache.invalidateTaggedItems(getSiblingsTag(page));
		}
	}
	
}

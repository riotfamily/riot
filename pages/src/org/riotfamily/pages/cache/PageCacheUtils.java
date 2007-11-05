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

import org.riotfamily.cachius.Cache;
import org.riotfamily.cachius.TaggingContext;
import org.riotfamily.pages.model.Page;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public final class PageCacheUtils {

	private static final String PAGE_PREFIX = Page.class.getName() + '#';
	
	private static final String TOP_LEVEL_PAGES_PREFIX = Page.class.getName() 
			+ ":topLevelPages#";
	
	private static final String CHILD_PAGE_PREFIX = Page.class.getName() 
			+ ":childPages#";
	
	private PageCacheUtils() {
	}
	
	private static String getPageTag(Page page) {
		return PAGE_PREFIX + page.getId();
	}
	
	private static String getSiblingsTag(Page page) {
		if (page.getParentPage() == null) {
			return TOP_LEVEL_PAGES_PREFIX + page.getSite().getId();
		}
		return getChildPagesTag(page.getParentPage());
	}
	
	private static String getChildPagesTag(Page page) {
		return CHILD_PAGE_PREFIX + page.getId();
	}
	
	public static void addPageTag(TaggingContext taggingContext, Page page) {
		if (taggingContext != null) {
			taggingContext.addTag(getPageTag(page));
		}
	}
	
	public static void addChildPagesTag(TaggingContext taggingContext, Page page) {
		if (taggingContext != null) {
			taggingContext.addTag(getChildPagesTag(page));
		}
	}
	
	public static void addSiblingsTag(TaggingContext taggingContext, Page page) {
		if (taggingContext != null) {
			taggingContext.addTag(getSiblingsTag(page));
		}
	}
	
	public static void invalidateSiblings(Cache cache, Page page) {
		if (cache != null) {
			cache.invalidateTaggedItems(getSiblingsTag(page));
		}
	}
	
}

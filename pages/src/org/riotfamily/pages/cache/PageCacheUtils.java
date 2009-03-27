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

import org.riotfamily.cachius.CacheService;
import org.riotfamily.cachius.TaggingContext;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public final class PageCacheUtils {

	private static final String SITE_PREFIX = Site.class.getName() + '#';

	private static final String NODE_PREFIX = Page.class.getName() + '#';
	
	private PageCacheUtils() {
	}

	public static String getSiteTag(Site site) {
		return SITE_PREFIX + site.getId();
	}

	public static void addSiteTag(Site site) {
		TaggingContext.tag(getSiteTag(site));
	}

	public static String getNodeTag(Page page) {
		return NODE_PREFIX + page.getId();
	}
		
	public static void addNodeTag(Page page) {
		TaggingContext.tag(getNodeTag(page));
	}
		
	public static void invalidateNode(CacheService cacheService, Page page) {
		if (cacheService != null) {
		    cacheService.invalidateTaggedItems(getNodeTag(page));
		}
	}
	
	public static void invalidateSite(CacheService cacheService, Site site) {
		if (cacheService != null) {
		    cacheService.invalidateTaggedItems(getSiteTag(site));
		}
	}
	
}

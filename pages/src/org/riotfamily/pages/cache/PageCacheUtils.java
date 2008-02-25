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
import org.riotfamily.pages.model.PageNode;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public final class PageCacheUtils {

	private static final String NODE_PREFIX = PageNode.class.getName() + '#';
	
	private PageCacheUtils() {
	}
	
	public static String getNodeTag(PageNode node) {
		return NODE_PREFIX + node.getId();
	}
	
	public static void addNodeTag(TaggingContext context, PageNode node) {
		if (context != null) {
			context.addTag(getNodeTag(node));
		}
	}
	
	public static void addNodeTag(HttpServletRequest request, PageNode node) {
		TaggingContext.tag(request, getNodeTag(node));
	}
	
	public static void addNodeTag(PageNode node) {
		TaggingContext.tag(getNodeTag(node));
	}
		
	public static void invalidateNode(Cache cache, PageNode node) {
		if (cache != null) {
			cache.invalidateTaggedItems(getNodeTag(node));
		}
	}
	
}

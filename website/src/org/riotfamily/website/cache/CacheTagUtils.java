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
package org.riotfamily.website.cache;

import java.io.Serializable;

import org.riotfamily.cachius.CacheService;
import org.riotfamily.cachius.TaggingContext;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public final class CacheTagUtils {

	private CacheTagUtils() {
	}
	
	public static void tag(Class<?> clazz, Serializable id) {
		TaggingContext ctx = TaggingContext.getContext();
		if (ctx != null) {
			if (!ctx.hasTag(clazz.getName())) {
				ctx.addTag(clazz.getName() + '#' + id);
			}
		}
	}
	
	public static void tag(Class<?> clazz) {
		TaggingContext.tag(clazz.getName());
	}
	
	public static void invalidate(CacheService cacheService, Class<?> clazz) {
		if (cacheService != null) {
		    cacheService.invalidateTaggedItems(clazz.getName());
		}
	}
	
	public static void invalidate(CacheService cacheService, Class<?> clazz, Object objectId) {
		if (cacheService != null) {
		    cacheService.invalidateTaggedItems(clazz.getName());
		    cacheService.invalidateTaggedItems(clazz.getName() + '#' + objectId);
		}
	}
		
}

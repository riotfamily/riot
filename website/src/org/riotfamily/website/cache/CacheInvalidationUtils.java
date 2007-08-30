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

import org.riotfamily.cachius.Cache;
import org.riotfamily.riot.dao.RiotDao;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public final class CacheInvalidationUtils {

	private CacheInvalidationUtils() {
	}
		
	public static void invalidate(Cache cache, Class clazz) {
		if (cache != null) {
			cache.invalidateTaggedItems(clazz.getName());
		}
	}
	
	public static void invalidate(Cache cache, Class clazz, Object objectId) {
		if (cache != null) {
			cache.invalidateTaggedItems(clazz.getName());
			cache.invalidateTaggedItems(clazz.getName() + '#' + objectId);
		}
	}
	
	public static void invalidate(Cache cache, RiotDao dao) {
		invalidate(cache, dao.getEntityClass());
	}
	
	public static void invalidate(Cache cache, RiotDao dao, Object object) {
		invalidate(cache, dao.getEntityClass(), dao.getObjectId(object));
	}
	
}

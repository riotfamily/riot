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
import org.riotfamily.cachius.CachiusContext;
import org.riotfamily.common.hibernate.ActiveRecord;
import org.riotfamily.common.hibernate.ActiveRecordUtils;
import org.springframework.util.ClassUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public final class CacheTagUtils {

	private CacheTagUtils() {
	}
	
	public static String getTag(Class<?> clazz) {
		return ClassUtils.getUserClass(clazz).getName();	
	}
	
	public static String getTag(Class<?> clazz, Serializable id) {
		return getTag(clazz) + '#' + id; 
	}
		
	public static void tag(Class<?> clazz, Serializable id) {
		CachiusContext.tag(getTag(clazz, id));
	}
	
	public static void tag(ActiveRecord record) {
		Serializable id = ActiveRecordUtils.getId(record);
		CachiusContext.tag(getTag(record.getClass(), id));
	}
	
	public static void tag(Class<?> clazz) {
		CachiusContext.tag(getTag(clazz));
	}
	
	public static void tag(String className) throws ClassNotFoundException {
		Class<?> clazz = Class.forName(className);
		tag(clazz);
	}
	
	public static void invalidate(CacheService cacheService, Class<?> clazz) {
		if (cacheService != null) {
		    cacheService.invalidateTaggedItems(getTag(clazz));
		}
	}
	
	public static void invalidate(CacheService cacheService, Class<?> clazz, Serializable objectId) {
		if (cacheService != null) {
		    cacheService.invalidateTaggedItems(getTag(clazz));
		    cacheService.invalidateTaggedItems(getTag(clazz, objectId));
		}
	}
	
	public static void invalidate(CacheService cacheService, ActiveRecord record) {
		invalidate(cacheService, record.getClass(), ActiveRecordUtils.getId(record));
	}
		
}

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
package org.riotfamily.components.cache;

import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.cachius.Cache;
import org.riotfamily.cachius.TaggingContext;
import org.riotfamily.components.config.component.Component;
import org.riotfamily.components.model.ComponentList;
import org.riotfamily.components.model.ComponentVersion;
import org.riotfamily.components.model.Location;
import org.riotfamily.components.model.VersionContainer;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public final class ComponentCacheUtils {

	private static String getContainerTag(VersionContainer container, 
			boolean editMode) {
		
		return VersionContainer.class.getName()
				+ '#' + container.getId() 
				+ (editMode ? "-preview" : "-live");
	}
	
	public static void addContainerTags(HttpServletRequest request, 
			VersionContainer container, boolean editMode) {
		
		TaggingContext.tag(request, getContainerTag(container, editMode));
	}
	
	public static void invalidateContainer(Cache cache, 
			VersionContainer container, boolean editMode) {
		
		cache.invalidateTaggedItems(getContainerTag(container, editMode));
	}
	
	public static void addListTags(HttpServletRequest request, 
			VersionContainer container) {
		
		ComponentList list = container.getList();
		if (list != null) {
			addListTags(request, list.getLocation());
		}
	}
	
	public static void addListTags(HttpServletRequest request, 
			Location location) {
		
		TaggingContext.tag(request, location.toString());
	}
	
	public static void invalidateList(Cache cache, ComponentList list) {
		cache.invalidateTaggedItems(list.getLocation().toString());
	}
	
	public static void addComponentTags(HttpServletRequest request, 
			Component component, ComponentVersion version) {
		
		Collection tags = version.getCacheTags();
		if (tags != null) {
			Iterator it = tags.iterator();
			while (it.hasNext()) {
				String tag = (String) it.next();
				TaggingContext.tag(request, tag);
			}
		}
	}
}

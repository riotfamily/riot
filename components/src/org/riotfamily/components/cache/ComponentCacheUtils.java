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
import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.ComponentList;
import org.riotfamily.components.model.Content;
import org.riotfamily.components.model.ComponentListLocation;
import org.riotfamily.components.model.ContentContainer;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public final class ComponentCacheUtils {

	private static String getContainerTag(ContentContainer container, boolean preview) {
		return ContentContainer.class.getName()
				+ '#' + container.getId() 
				+ (preview ? "-preview" : "-live");
	}
	
	public static void addContainerTags(HttpServletRequest request, 
			ContentContainer container, boolean preview) {
		
		TaggingContext.tag(request, getContainerTag(container, preview));
	}
	
	public static void invalidateContainer(Cache cache, 
			ContentContainer container, boolean preview) {
		
		cache.invalidateTaggedItems(getContainerTag(container, preview));
	}
	
	public static void addListTags(HttpServletRequest request, 
			Component component) {
		
		ComponentList list = component.getList();
		if (list != null) {
			addListTags(request, list.getLocation());
		}
	}
	
	public static void addListTags(HttpServletRequest request, 
			ComponentListLocation location) {
		
		TaggingContext.tag(request, location.toString());
	}
	
	public static void invalidateList(Cache cache, ComponentList list) {
		cache.invalidateTaggedItems(list.getLocation().toString());
	}
	
	public static void addContentTags(HttpServletRequest request, 
			Content version) {

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

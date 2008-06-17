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

import org.riotfamily.cachius.CacheService;
import org.riotfamily.cachius.TaggingContext;
import org.riotfamily.components.model.ComponentList;
import org.riotfamily.components.model.ComponentListLocation;
import org.riotfamily.components.model.Content;
import org.riotfamily.components.model.ContentContainer;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public final class ComponentCacheUtils {

	private ComponentCacheUtils() {
	}
	
	/**
	 * Returns the tag for the given container.
	 */
	private static String getContainerTag(ContentContainer container, 
			boolean preview) {
		
		return ContentContainer.class.getName() + '#' + container.getId() 
				+ (preview ? "-preview" : "-live");
	}
	
	public static void addContainerTags(HttpServletRequest request, 
			ContentContainer container, boolean preview) {
		
		TaggingContext.tag(request, getContainerTag(container, preview));
		addContentTags(request, container.getContent(preview));
	}

	private static void addContentTags(HttpServletRequest request, 
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
	
	/**
	 * Invalidates the live and preview version of the container.
	 */
	public static void invalidateContainer(CacheService cacheService, ContentContainer container) {
		cacheService.invalidateTaggedItems(getContainerTag(container, false));
		cacheService.invalidateTaggedItems(getContainerTag(container, true));
	}
	
	/**
	 * Invalidates the preview version of the container.
	 */
	public static void invalidatePreviewVersion(CacheService cacheService, ContentContainer container) {
		cacheService.invalidateTaggedItems(getContainerTag(container, true));
	}
	
	/**
	 * Returns the tag for the given list.
	 */
	private static String getListTag(ComponentList list, boolean preview) {
		if (list.getParent() != null) {
			return "child://" + list.getParent().getId() + "#" 
					+ list.getLocation().getSlot()
					+ (preview ? "-preview" : "-live");
		}
		return getListTag(list.getLocation(), preview);
	}
	
    /**
     * Returns the tag for the given list location.
     */
    private static String getListTag(ComponentListLocation location,
        boolean preview) {
        
        return location.toString() + (preview ? "-preview" : "-live");
    }

    public static void addListTag(HttpServletRequest request, 
			ComponentList list, boolean preview) {
		
		TaggingContext.tag(request, getListTag(list, preview));
	}

    public static void addListTag(HttpServletRequest request, 
            ComponentListLocation location, boolean preview) {
        
        TaggingContext.tag(request, getListTag(location, preview));
    }

	/**
	 * Invalidates the live and preview version of the given list.
	 */
	public static void invalidateList(CacheService cacheService, ComponentList list) {
		cacheService.invalidateTaggedItems(getListTag(list, true));
		cacheService.invalidateTaggedItems(getListTag(list, false));
	}
	
	/**
	 * Invalidates the preview version of the given list.
	 */
	public static void invalidatePreviewList(CacheService cacheService, ComponentList list) {
		cacheService.invalidateTaggedItems(getListTag(list, true));
	}
	
}

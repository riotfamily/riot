package org.riotfamily.components.cache;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.cachius.CacheService;
import org.riotfamily.cachius.CachiusContext;
import org.riotfamily.components.model.ContentContainer;
import org.riotfamily.components.support.EditModeUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public final class ComponentCacheUtils {

	private ComponentCacheUtils() {
	}
	
	/**
	 * Returns the tag for the given container id.
	 */
	private static String getContainerTag(ContentContainer container, 
			boolean preview) {
		
		return ContentContainer.class.getName() + '#' + container.getId() 
				+ (preview ? "-preview" : "-live");
	}
	
	public static void addContainerTags(ContentContainer container, HttpServletRequest request) {
		addContainerTags(container, EditModeUtils.isPreview(request, container));
	}
	
	public static void addContainerTags(ContentContainer container, boolean preview) {
		CachiusContext.tag(getContainerTag(container, preview));
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

}

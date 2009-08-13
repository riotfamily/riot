/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
	
	/*
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
	
	public static void invalidateContainer(CacheService cacheService, ContentContainer container) {
		cacheService.invalidateTaggedItems(getContainerTag(container, false));
		cacheService.invalidateTaggedItems(getContainerTag(container, true));
	}
	
	public static void invalidatePreviewVersion(CacheService cacheService, ContentContainer container) {
		cacheService.invalidateTaggedItems(getContainerTag(container, true));
	}
	*/
}

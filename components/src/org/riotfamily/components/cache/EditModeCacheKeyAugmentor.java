package org.riotfamily.components.cache;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.cachius.servlet.CacheKeyAugmentor;
import org.riotfamily.components.support.EditModeUtils;
import org.riotfamily.core.security.AccessController;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class EditModeCacheKeyAugmentor implements CacheKeyAugmentor {

	public void augmentCacheKey(StringBuffer key, HttpServletRequest request) {
		if (AccessController.isAuthenticatedUser()) {
			if (EditModeUtils.isEditMode(request)) {
				key.insert(0, "edit:");
			}
			else if (EditModeUtils.isPreviewMode(request)) {
				key.insert(0, "preview:");				
			}
			else {
				key.insert(0, "live:");				
			}
		}
	}
}

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

import org.riotfamily.common.cache.CacheKeyAugmentor;
import org.riotfamily.components.support.EditModeUtils;
import org.riotfamily.core.security.AccessController;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class EditModeCacheKeyAugmentor implements CacheKeyAugmentor {

	public void augmentCacheKey(StringBuilder key, HttpServletRequest request) {
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

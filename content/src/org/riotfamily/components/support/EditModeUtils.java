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
package org.riotfamily.components.support;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.components.model.ContentContainer;
import org.riotfamily.core.security.AccessController;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public final class EditModeUtils {

	private EditModeUtils() {
	}
	
	private static String getMode(HttpServletRequest request) {
		return request.getParameter("riotMode");
	}
	
	private static List<String> getContainerIds(HttpServletRequest request) {
		String[] s = request.getParameterValues("riotContainer");
		if (s == null) {
			return Collections.emptyList();
		}
		return Arrays.asList(s);
	}
	
	public static boolean isEditable(String action, Object object, HttpServletRequest request) {
		return AccessController.isGranted(action, object, request);
	}
	
	public static boolean isEditMode(HttpServletRequest request) {
		return AccessController.isAuthenticatedUser() 
				&& getMode(request) == null;
	}
	
	public static boolean isLiveMode(HttpServletRequest request) {
		return AccessController.isAuthenticatedUser() 
				&& "live".equals(getMode(request));
	}
	
	public static boolean isPreviewMode(HttpServletRequest request) {
		return AccessController.isAuthenticatedUser() 
				&& "preview".equals(getMode(request));
	}
	
	public static boolean isPreview(HttpServletRequest request, 
			ContentContainer container) {
		
		if (isEditMode(request)) {
			return true;
		}
		if (!isPreviewMode(request)) {
			return false;
		}
		if (container == null || container.getId() == null) {
			return true;
		}
		String id = container.getId().toString();
		return getContainerIds(request).contains(id);
	}
		
	
}

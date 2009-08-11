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

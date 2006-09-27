package org.riotfamily.pages.component.preview;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.riot.security.AccessController;

public class DefaultViewModeResolver implements ViewModeResolver {

	public boolean isPreviewMode(HttpServletRequest request) {
		if (AccessController.isAuthenticatedUser()) {
			return request.getParameter("liveMode") == null;
		}
		return false;
	}

}

package org.riotfamily.pages.component.preview;

import javax.servlet.http.HttpServletRequest;

public class ParamViewModeResolver implements ViewModeResolver {

	public boolean isPreviewMode(HttpServletRequest request) {
		return request.getParameter("live") == null;
	}

}

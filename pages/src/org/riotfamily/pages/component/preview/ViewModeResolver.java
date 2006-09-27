package org.riotfamily.pages.component.preview;

import javax.servlet.http.HttpServletRequest;

public interface ViewModeResolver {

	public boolean isPreviewMode(HttpServletRequest request);

}

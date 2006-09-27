package org.riotfamily.common.web.view.freemarker;

import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

public class RiotFreeMarkerViewResolver extends FreeMarkerViewResolver {

	private boolean allowModelOverride;
	
	private boolean freeMarkerServletMode;
	
	public void setAllowModelOverride(boolean allowModelOverride) {
		this.allowModelOverride = allowModelOverride;
	}

	public void setFreeMarkerServletMode(boolean freeMarkerServletMode) {
		this.freeMarkerServletMode = freeMarkerServletMode;
	}

	protected Class requiredViewClass() {
		return RiotFreeMarkerView.class;
	}
	
	protected AbstractUrlBasedView buildView(String viewName) throws Exception {
		RiotFreeMarkerView view = (RiotFreeMarkerView) super.buildView(viewName);
		view.setAllowModelOverride(allowModelOverride);
		view.setFreeMarkerServletMode(freeMarkerServletMode);
		return view;
	}
}

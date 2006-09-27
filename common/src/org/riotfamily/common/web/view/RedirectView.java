package org.riotfamily.common.web.view;

import java.util.Collections;
import java.util.Map;

public class RedirectView extends 
		org.springframework.web.servlet.view.RedirectView {

	private boolean appendModelAsParams = false;
	
	public RedirectView() {
	}
	
	public RedirectView(String url) {
		super(url);
	}

	public RedirectView(String url, boolean contextRelative, 
			boolean http10Compatible) {
		
		super(url, contextRelative, http10Compatible);
	}

	public void setAppendModelAsParams(boolean appendModelAsParams) {
		this.appendModelAsParams = appendModelAsParams;
	}

	protected Map queryProperties(Map model) {
		if (appendModelAsParams && model != null) {
			return super.queryProperties(model);
		}
		return Collections.EMPTY_MAP;
	}

}

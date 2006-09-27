package org.riotfamily.pages.component.impl;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.pages.component.ComponentVersion;
import org.springframework.web.util.WebUtils;

/**
 * Component implementation that uses a RequestDispatcher to perform the
 * rendering. The configured url will be included and the properties of
 * the ComponentVersion that is to be rendered will be exposed as request
 * attributes.
 */
public class IncludeComponent extends AbstractComponent {

	private String uri;
	
	private boolean dynamic = true;
	
	public void setUri(String uri) {
		this.uri = uri;
	}

	protected void renderInternal(ComponentVersion componentVersion, 
			String positionClassName, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		Map snapshot = ServletUtils.takeAttributesSnapshot(request);
		WebUtils.exposeRequestAttributes(request, buildModel(componentVersion));
		request.setAttribute(POSITION_CLASS, positionClassName);
		request.setAttribute(COMPONENT_ID, componentVersion.getId());
		request.getRequestDispatcher(uri).include(request, response);
		ServletUtils.restoreAttributes(request, snapshot);
	}

	public boolean isDynamic() {
		return this.dynamic;
	}
	
	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}
	
}

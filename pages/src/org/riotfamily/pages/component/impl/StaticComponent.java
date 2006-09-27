package org.riotfamily.pages.component.impl;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.support.IOUtils;
import org.riotfamily.pages.component.ComponentVersion;
import org.springframework.core.io.Resource;

/**
 * Component implementation that renders the content of a static resource. 
 */
public class StaticComponent extends AbstractComponent {

	private Resource location;

	public void setLocation(Resource resource) {
		this.location = resource;
	}
	
	protected void renderInternal(ComponentVersion componentVersion, 
			String positionClassName, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		Reader in = new InputStreamReader(location.getInputStream());
		Writer out = response.getWriter();
		IOUtils.copy(in, out);
	}

}

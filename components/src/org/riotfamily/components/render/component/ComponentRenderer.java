package org.riotfamily.components.render.component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.components.model.Component;

public interface ComponentRenderer {

	/**
	 * Renders the given ComponentVersion.
	 */
	public void render(Component component, HttpServletRequest request, 
			HttpServletResponse response) throws Exception;

}

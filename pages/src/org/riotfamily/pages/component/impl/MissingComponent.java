package org.riotfamily.pages.component.impl;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.pages.component.ComponentVersion;

/**
 * Failsafe component that is rendered when an unknown component type is 
 * encountered. This may happen if a component is removed from
 * the ComponentRepository and a reference still exists in the database.  
 */
public class MissingComponent extends AbstractComponent {
	
	private String type;
	
	public MissingComponent(String type) {
		this.type = type;
	}

	protected void renderInternal(ComponentVersion component, 
			String positionClassName, HttpServletRequest request, 
			HttpServletResponse response) throws IOException {
		
		PrintWriter out = response.getWriter();
		out.write("No such Component: <code>" + type + "</code>.");
	}
	
}
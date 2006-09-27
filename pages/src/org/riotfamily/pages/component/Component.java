package org.riotfamily.pages.component;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.pages.component.property.PropertyProcessor;



public interface Component {

	/**
	 * Indicates whether the content rendered by the component depends on
	 * anything other but the components internal data.
	 */
	public boolean isDynamic();
	
	public void addPropertyProcessor(PropertyProcessor propertyProcessor);
	
	public Map buildModel(ComponentVersion version);
	
	public void updateProperties(ComponentVersion version, Map model);
	
	public ComponentVersion copy(ComponentVersion source);
	
	public void delete(ComponentVersion source);
	
	/**
	 * Renders the given ComponentVersion.
	 */
	public void render(ComponentVersion version, String positionClassName, 
			ComponentListConfiguration config, HttpServletRequest request, 
			HttpServletResponse response) throws IOException;
}

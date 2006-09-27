package org.riotfamily.forms.resource;

import java.io.PrintWriter;
import java.util.Collection;


/**
 * Interface that can be implemented in order to load external resources like
 * JavaScript libraries or Cascading Stylesheets (CSS).
 * 
 * @see org.riotfamily.forms.resource.ScriptResource
 * @see org.riotfamily.forms.resource.StylesheetResource
 */
public interface FormResource {
	
	/**
	 * Renders JavaScript code to load the resource. After this method is 
	 * invoked the resource is automatically added to the collection of loaded
	 * resources. 
	 *  
	 * @param writer PrintWriter to be used for rendering
	 * @param loadedResources Collection of already loaded resources
	 */
	public void renderLoadingCode(PrintWriter writer, Collection loadedResources);

}

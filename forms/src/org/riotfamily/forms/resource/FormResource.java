package org.riotfamily.forms.resource;



/**
 * Interface for external resources like JavaScript libraries or 
 * Cascading Stylesheets (CSS).
 * 
 * @see org.riotfamily.forms.resource.ScriptResource
 * @see org.riotfamily.forms.resource.StylesheetResource
 */
public interface FormResource {
	
	public void accept(ResourceVisitor visitor);

	public String getUrl();

}

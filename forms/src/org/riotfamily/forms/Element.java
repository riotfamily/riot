package org.riotfamily.forms;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;


/**
 * Interface to be implemented by all form elements. If you want to create a 
 * new element you will most likely want to subclass one of the abstract base
 * classes in the <code>de.neteye.forms.element.support</code> package.
 */
public interface Element {
	
	/**
	 * Sets the element's unique id. Ids are assigned when an element is
	 * registered with a form.
	 * 
	 * @see Form#registerElement(Element)
	 */
	public void setId(String id);

	/**
	 * Returns the previously assigned id.
	 */
	public String getId();
	
	/**
	 * Sets a reference to the form that contains the element.
	 */
	public void setForm(Form form);

	/**
	 * Returns the form that contains the element. 
	 */
	public Form getForm();
	
	/**
	 * 
	 */
	public void setFormContext(FormContext formContext);
	
	/**
	 * Sets the element's parent. E.g. the parent element is taken into account
	 * to determine the enabled state of the element.  
	 */
	public void setParent(Element parent);
	
	/**
	 * Returns the element's parent.
	 */
	public Element getParent();
	
	/**
	 * This method is invoked whenever a http request needs to be processed.
	 * Elements may impelement this method to change their internal state 
	 * according to parameters found in the request. 
	 */
	public void processRequest(HttpServletRequest request);
		
	/**
	 * Renders the element to the given writer.
	 */
	public void render(PrintWriter writer);
	
	/**
	 * Focuses the element.
	 */
	public void focus();
	
	/**
	 * Returns whether the element will accept user input. The state should
	 * be considered during rendering, i.e. disabled elements should look
	 * different than enabled ones.
	 */
	public boolean isEnabled();
	
	/**
	 * Enables (or disables) the element.
	 */
	public void setEnabled(boolean enabled);
	
	/**
	 * Returns whether the element is mandatory and must be filled out by 
	 * the user.
	 */
	public boolean isRequired();
	
	/**
	 * Sets whether the element is required.
	 */
	public void setRequired(boolean required);
			
}
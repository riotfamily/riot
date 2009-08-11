package org.riotfamily.forms;





/**
 * Factory interface to create form elements. 
 */
public interface ElementFactory {

	public Element createElement(Element parent, Form form, boolean bind);
	
}

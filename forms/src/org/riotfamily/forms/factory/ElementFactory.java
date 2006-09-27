package org.riotfamily.forms.factory;

import org.riotfamily.forms.Element;
import org.riotfamily.forms.Form;


/**
 * Factory interface to create form elements. 
 */
public interface ElementFactory {

	public Element createElement(Element parent, Form form);
	
}

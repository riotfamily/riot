package org.riotfamily.forms.element;

import org.riotfamily.forms.Element;

/**
 * Interface to be implemented by elements that can have child elements.
 */
public interface ContainerElement extends Element {

	public void addElement(Element element);
	
	public void removeElement(Element element);

}

package org.riotfamily.forms;

import java.util.List;



/**
 * Interface to be implemented by elements that can have child elements.
 */
public interface ContainerElement extends Element {

	public void addElement(Element element);
	
	public void removeElement(Element element);
	
	public List<Element> getElements();

}

package org.riotfamily.forms.factory;

import java.util.List;

/**
 * Interface to be implemented by element factories that create 
 * {@link org.riotfamily.forms.element.ContainerElement container elements}.
 */
public interface ContainerElementFactory {

	public void addChildFactory(ElementFactory factory);
	
	public List getChildFactories();
	
}

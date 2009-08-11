package org.riotfamily.forms.factory;

import java.util.List;

import org.riotfamily.forms.ElementFactory;


/**
 * Interface to be implemented by element factories that create 
 * {@link org.riotfamily.forms.ContainerElement container elements}.
 */
public interface ContainerElementFactory {

	public void addChildFactory(ElementFactory factory);
	
	public List<ElementFactory> getChildFactories();
	
}

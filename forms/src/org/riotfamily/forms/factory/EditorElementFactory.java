package org.riotfamily.forms.factory;

import org.riotfamily.forms.ElementFactory;


/**
 * Interface to be implemented by element factories that create
 * {@link org.riotfamily.forms.Editor editor elements}.
 */
public interface EditorElementFactory extends ElementFactory {

	public String getBind();
	
}

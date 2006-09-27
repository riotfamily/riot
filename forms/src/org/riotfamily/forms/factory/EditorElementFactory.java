package org.riotfamily.forms.factory;

/**
 * Interface to be implemented by element factories that create
 * {@link org.riotfamily.forms.bind.Editor editor elements}.
 */
public interface EditorElementFactory extends ElementFactory {

	public String getBind();
	
}

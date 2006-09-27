package org.riotfamily.forms.bind;


/**
 * Editor that is capable of editing Java Beans.
 */
public interface BeanEditor {

	public void setBeanClass(Class beanClass);
	
	public void bind(Editor editor, String property);
	
	public EditorBinder getEditorBinder();
	
}

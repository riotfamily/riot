package org.riotfamily.forms;

/**
 * Editor that is capable of editing Java Beans.
 */
public interface BeanEditor extends NestedEditor {

	public void setBeanClass(Class<?> beanClass);
	
	public void bind(Editor editor, String property);
	
}

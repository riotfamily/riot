package org.riotfamily.forms;

import java.beans.PropertyEditor;
import java.util.Collection;
import java.util.Map;

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public interface EditorBinder extends PropertyEditorRegistry {

	public Map<String, EditorBinding> getBindings();

	public EditorBinder replace(EditorBinder previousBinder);
	
	public Class<?> getBeanClass();
	
	public Object getBackingObject();

	public void setBackingObject(Object backingObject);

	public boolean isEditingExistingBean();

	public Object getPropertyValue(String property);

	public void setPropertyValue(String property, Object value);

	public Class<?> getPropertyType(String property);
	
	public PropertyEditor getPropertyEditor(Class<?> type, String propertyPath);

	/**
	 * Binds the given editor to the property with the specified name.
	 *
	 * @param editor the editor to bind
	 * @param property the name of the property the editor is to be bound to
	 */
	public void bind(Editor editor, String property);

	/**
	 * Returns the editor that is bound to the given property.
	 */
	public Editor getEditor(String property);

	/**
	 * Returns the names of all properties an editor is bound to.
	 * @since 6.4
	 */
	public String[] getBoundProperties();

	/**
	 * Initializes each editor with the property value it is bound to or
	 * <code>null<code> if the backingObject is not set.
	 *
	 * @see Editor#setValue(Object)
	 */
	public void initEditors();

	/**
	 * Sets the properties of the backingObject to the values provided by the
	 * corresponding editor. If the backingObject is <code>null</code> a new
	 * instance is created.
	 *
	 * @return the populated backingObject
	 */
	public Object populateBackingObject();
	
	public void registerPropertyEditors(
			Collection<PropertyEditorRegistrar> registrars);

}
package org.riotfamily.pages.riot.form;

import java.beans.PropertyEditor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.riotfamily.forms.Editor;
import org.riotfamily.forms.EditorBinder;
import org.riotfamily.forms.EditorBinding;
import org.springframework.beans.PropertyEditorRegistrar;

/**
 * EditorBinder for {@link AbstractLocalizedElement localized elements}
 * that returns null, if the value is not 
 * {@link AbstractLocalizedElement#isOverwrite() overwritten}.
 */
public class LocalizedEditorBinder implements EditorBinder {

	private EditorBinder delegate;
	
	private Map<EditorBinding, AbstractLocalizedElement> elements = 
			new HashMap<EditorBinding, AbstractLocalizedElement>();
	
	public LocalizedEditorBinder(EditorBinder delegate) {
		this.delegate = delegate;
	}

	public void registerElement(EditorBinding binding, 
			AbstractLocalizedElement editor) {
		
		elements.put(binding, editor);
	}
	
	private boolean isOverwrite(EditorBinding binding) {
		AbstractLocalizedElement ele = elements.get(binding);
		return ele.isOverwrite();
	}
	
	private Object getValue(EditorBinding binding) {
		return isOverwrite(binding) ? binding.getEditor().getValue() : null;
	}
	
	public Object populateBackingObject() {
		for (EditorBinding binding : getBindings()) {
			setPropertyValue(binding.getProperty(), getValue(binding));
		}
		return getBackingObject();
	}

	public void bind(Editor editor, String property) {
		delegate.bind(editor, property);
	}

	@SuppressWarnings("unchecked")
	public PropertyEditor findCustomEditor(Class requiredType, String propertyPath) {
		return delegate.findCustomEditor(requiredType, propertyPath);
	}

	public Object getBackingObject() {
		return delegate.getBackingObject();
	}

	public Class<?> getBeanClass() {
		return delegate.getBeanClass();
	}

	public List<EditorBinding> getBindings() {
		return delegate.getBindings();
	}

	public String[] getBoundProperties() {
		return delegate.getBoundProperties();
	}

	public Editor getEditor(String property) {
		return delegate.getEditor(property);
	}

	public Class<?> getPropertyType(String property) {
		return delegate.getPropertyType(property);
	}

	public Object getPropertyValue(String property) {
		return delegate.getPropertyValue(property);
	}

	public void initEditors() {
		delegate.initEditors();
	}

	public boolean isEditingExistingBean() {
		return delegate.isEditingExistingBean();
	}

	@SuppressWarnings("unchecked")
	public void registerCustomEditor(Class requiredType,
			PropertyEditor propertyEditor) {
		delegate.registerCustomEditor(requiredType, propertyEditor);
	}

	@SuppressWarnings("unchecked")
	public void registerCustomEditor(Class requiredType, String propertyPath,
			PropertyEditor propertyEditor) {
		delegate.registerCustomEditor(requiredType, propertyPath,
				propertyEditor);
	}

	public void registerPropertyEditors(PropertyEditorRegistrar[] registrars) {
		delegate.registerPropertyEditors(registrars);
	}

	public EditorBinder replace(EditorBinder previousBinder) {
		delegate.replace(previousBinder);
		return this;
	}

	public void setBackingObject(Object backingObject) {
		delegate.setBackingObject(backingObject);
	}

	public void setPropertyValue(String property, Object value) {
		delegate.setPropertyValue(property, value);
	}
	
}

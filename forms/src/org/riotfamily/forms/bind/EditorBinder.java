package org.riotfamily.forms.bind;

import java.beans.PropertyEditor;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.beans.MapAccessor;
import org.riotfamily.common.beans.ProtectedPropertyAccessor;
import org.riotfamily.common.beans.propertyeditors.SqlDateEditor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistrySupport;
import org.springframework.beans.propertyeditors.CustomDateEditor;


/**
 * This class is used to bind a form element to the property of a bean. The
 * form element must implement the {@link org.riotfamily.forms.bind.Editor} 
 * interface.
 */
public class EditorBinder extends PropertyEditorRegistrySupport {

	private Log log = LogFactory.getLog(EditorBinder.class);

	/** List of {@link EditorBinding editor bindings} */
	private List bindings = new LinkedList();

	private Class beanClass;
	
	private Object backingObject;
	
	private PropertyAccessor propertyAccessor;
	
	private boolean editingExistingBean;

	private EditorBinding parent;
	
	/**
	 * Creates a new EditorBinder for the given class.
	 */
	public EditorBinder(Class beanClass) {
		registerDefaultEditors();
		registerCustomEditor(java.sql.Date.class,new SqlDateEditor());
		registerCustomEditor(Date.class, new CustomDateEditor(
				new SimpleDateFormat("yyyy-MM-dd"), false));
		
		this.beanClass = beanClass;
		setBackingObject(null);
	}
	
	public Class getBeanClass() {
		return beanClass;
	}

	public Object getBackingObject() {
		return backingObject;
	}
	
	public void setBackingObject(Object backingObject) {
		if (backingObject != null) {
			this.backingObject = backingObject;
			editingExistingBean = true;
		}
		else {
			this.backingObject = BeanUtils.instantiateClass(beanClass);
			editingExistingBean = false;
		}
		propertyAccessor = createPropertyAccessor();
		
	}
	
	protected PropertyAccessor createPropertyAccessor() {
		if (Map.class.isAssignableFrom(beanClass)) {
			return new MapAccessor((Map) backingObject); 	
		}
		else {
			return new ProtectedPropertyAccessor(backingObject);
		}
	}
	
	public boolean isEditingExistingBean() {
		return editingExistingBean;
	}

	/**
	 * Binds the given editor to the property with the specified name.
	 * 
	 * @param editor the editor to bind
	 * @param property the name of the property the editor is to be bound to
	 */
	public void bind(Editor editor, String property) {
		log.debug("Binding " + editor + " to property " + property);
		EditorBinding eb = new EditorBindingImpl(this, editor, property);
		bindings.add(eb);
		editor.setEditorBinding(eb);		
	}

	/**
	 * Returns the editor that is bound to the given property. 
	 */
	public Editor getEditor(String property) {
		if (property != null) {
			int i = property.indexOf('.');
			if (i != -1) {
				String nested = property.substring(i + 1);
				property = property.substring(0, i);
				Editor editor = findEditorByProperty(property);
				if (editor instanceof BeanEditor) {
					BeanEditor be = (BeanEditor) editor;
					return be.getEditorBinder().findEditorByProperty(nested);
				}
				else {
					throw new InvalidPropertyException(getBeanClass(),
							property, "Nested editor must implement the " +
							"BeanEditor interface");
				}
			}
			return findEditorByProperty(property);
		}
		return null;
	}
	
	protected Editor findEditorByProperty(String property) {
		Iterator it = bindings.iterator();
		while (it.hasNext()) {
			EditorBinding binding = (EditorBinding) it.next();
			if (property.equals(binding.getProperty())) {
				return binding.getEditor();
			}
		}
		throw new InvalidPropertyException(getBeanClass(), property,
				"No editor bound to property");
	}
	
	public Object getPropertyValue(String property) {
		if (getBackingObject() != null) {
			return propertyAccessor.getPropertyValue(property);
		}
		return null;
	}
	
	public void setPropertyValue(String property, Object value) {
		propertyAccessor.setPropertyValue(property, value);
	}
	
	public Class getPropertyType(String property) {		
		return propertyAccessor.getPropertyType(property);		
	}
	
	public void registerPropertyEditors(PropertyEditorRegistrar[] registrars) {
		if (registrars != null) {
			for (int i = 0; i < registrars.length; i++) {
				registrars[i].registerCustomEditors(this);
			}
		}
	}
	
	/**
	 * Initializes each editor with the property value it is bound to or
	 * <code>null<code> if the backingObject is not set.
	 * 
	 * @see Editor#setValue(Object)
	 */
	public void initEditors() {
		Iterator it = bindings.iterator();
		while (it.hasNext()) {
			EditorBinding binding = (EditorBinding) it.next();
			Editor editor = binding.getEditor();
			editor.setValue(getPropertyValue(binding.getProperty()));
		}
	}

	/**
	 * Sets the properties of the backingObject to the values provided by the
	 * corresponding editor. If the backingObject is <code>null</code> a new
	 * instance is created.
	 * 
	 * @return the populated backingObject
	 */
	public Object populateBackingObject() {
		Iterator it = bindings.iterator();
		while (it.hasNext()) {
			EditorBinding binding = (EditorBinding) it.next();
			Object oldValue = binding.getValue();
			Object newValue = binding.getEditor().getValue();
			
			// Only set the property if it has been changed. This prevents
			// BeanWrapperImpl to perform type conversions which would fail
			// for collections managed by Hibernate.
			
			if (newValue != oldValue) {
				setPropertyValue(binding.getProperty(), newValue);
			}
		}
		return getBackingObject();
	}

	public EditorBinding getParent() {
		return this.parent;
	}

	public void setParent(EditorBinding parent) {
		this.parent = parent;
	}
	
	public PropertyEditor getPropertyEditor(Class type, String propertyPath) {
		PropertyEditor pe = findCustomEditor(type, propertyPath);
		if (pe == null) {
			pe = getDefaultEditor(type);
		}
		return pe;
	}
		
}
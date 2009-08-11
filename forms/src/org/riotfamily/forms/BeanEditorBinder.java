package org.riotfamily.forms;

import org.riotfamily.common.beans.property.ProtectedBeanWrapper;
import org.springframework.beans.BeanWrapper;
import org.springframework.util.Assert;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class BeanEditorBinder extends AbstractEditorBinder {

	private BeanWrapper beanWrapper;
	
	private boolean editingExistingBean;
	
	public BeanEditorBinder(Object backingObject) {
		Assert.notNull(backingObject);
		beanWrapper = new ProtectedBeanWrapper(backingObject);
		editingExistingBean = true;
	}
	
	public BeanEditorBinder(Class<?> beanClass) {
		beanWrapper = new ProtectedBeanWrapper(beanClass);
	}

	public Object getBackingObject() {
		return beanWrapper.getWrappedInstance();
	}

	public void setBackingObject(Object backingObject) {
		editingExistingBean = backingObject != null;
		if (backingObject != null) {
			beanWrapper = new ProtectedBeanWrapper(backingObject);
		}
		else {
			beanWrapper = new ProtectedBeanWrapper(getBeanClass());
		}
	}
	
	public boolean isEditingExistingBean() {
		return editingExistingBean;
	}
	
	public Class<?> getBeanClass() {
		return beanWrapper.getWrappedClass();
	}

	public Class<?> getPropertyType(String propertyName) {
		return beanWrapper.getPropertyType(propertyName);
	}
	
	public Object getPropertyValue(String propertyName) {
		return beanWrapper.getPropertyValue(propertyName);
	}

	public void setPropertyValue(String propertyName, Object value) {
		beanWrapper.setPropertyValue(propertyName, value);
	}

}

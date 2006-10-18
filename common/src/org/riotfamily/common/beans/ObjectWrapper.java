package org.riotfamily.common.beans;

import org.springframework.beans.PropertyAccessor;


public interface ObjectWrapper extends PropertyAccessor {

	public void setWrappedInstance(Object object);
	
	public Object getWrappedInstance();
	
	public Class getWrappedClass();

}

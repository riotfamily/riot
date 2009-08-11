package org.riotfamily.common.beans.property;

import org.springframework.beans.ConfigurablePropertyAccessor;


public interface ObjectWrapper extends ConfigurablePropertyAccessor {

	public void setObject(Object object);

	public Object getObject();

	public Class<?> getObjectClass();

}

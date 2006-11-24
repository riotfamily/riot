/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.util;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.reflect.Method;

import org.riotfamily.common.beans.DefaultPropertyEditorRegistry;
import org.riotfamily.common.beans.ProtectedBeanWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.PropertyAccessor;
import org.springframework.util.Assert;

/**
 * Utility class to access bean properties via relection.
 */
public final class PropertyUtils {

	private static DefaultPropertyEditorRegistry registry = 
			new DefaultPropertyEditorRegistry();
	
	private PropertyUtils() {
	}
	
	public static Object getProperty(Object bean, String name) {
		if (bean == null) {
			return null;
		}
		PropertyAccessor accessor = new ProtectedBeanWrapper(bean);
		return accessor.getPropertyValue(name);
	}
	
	public static String getPropertyAsString(Object bean, String name) {
		Object value = getProperty(bean, name);
		if (value != null) {
			if (!(value instanceof String)) {
				PropertyEditor pe = registry.findEditor(value.getClass());
				if (pe != null) {
					synchronized (pe) {
						pe.setValue(value);
						return pe.getAsText();
					}
				}
			}
			return value.toString();
		}
		return null;
	}
	
	public static Object convert(String s, Class targetClass) {
		if (targetClass.equals(String.class)) {
			return s;
		}
		PropertyEditor pe = registry.findEditor(targetClass);
		Assert.notNull(pe, "No PropertyEditor found for class: " + targetClass);
		synchronized (pe) {
			pe.setAsText(s);
			return pe.getValue();
		}
	}
	
	public static void setProperty(Object bean, String name, Object value) {
		PropertyAccessor accessor = new ProtectedBeanWrapper(bean);
		accessor.setPropertyValue(name, value);
	}
	
	public static void setPropertyAsString(Object bean, String name, String s) {
		Class type = getPropertyType(bean.getClass(), name);
		Object value = convert(s, type);
		setProperty(bean, name, value);
	}
	
	public static PropertyDescriptor getPropertyDescriptor(Class clazz,
            String property) {

        PropertyDescriptor[] pd = BeanUtils.getPropertyDescriptors(clazz);
        for (int i = 0; i < pd.length; i++) {
            if (pd[i].getName().equals(property)) {
                return pd[i];
            }
        }
        return null;
    }

    public static Class getPropertyType(Class clazz, String property) {
        PropertyDescriptor pd = getPropertyDescriptor(clazz, property);
        if (pd == null) {
            return null;
        }
        return pd.getPropertyType();
    }
    
    /**
     * Returns the (super-)class where the given property is decared. 
     */    
    public static Class getDeclaringClass(Class clazz, 
            String property) {

        PropertyDescriptor[] descriptors = 
        		BeanUtils.getPropertyDescriptors(clazz);
        
        for (int i = 0; i < descriptors.length; i++) {
            if (descriptors[i].getName().equals(property)) {
                Method getter = descriptors[i].getReadMethod();
                if (getter == null) {
                    return clazz; 
                }
                return getter.getDeclaringClass();
            }
        }
        return clazz;
    }
    
    public static Object newInstance(String className) {
		try {
			Class clazz = Class.forName(className);
			return BeanUtils.instantiateClass(clazz);
		}
		catch (ClassNotFoundException e) {
			throw new FatalBeanException(e.getMessage(), e);
		}
    }

}

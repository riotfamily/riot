/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.common.beans.property;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.riotfamily.common.util.Generics;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.NullValueInNestedPathException;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorUtils;
import org.springframework.core.GenericCollectionTypeResolver;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/**
 * Utility class to access bean properties via relection.
 */
public final class PropertyUtils {

	private static DefaultPropertyEditorRegistry registry =
			new DefaultPropertyEditorRegistry();

	private static Pattern expressionPattern = Pattern.compile(
			"\\$\\{(.*?)\\}");

	private PropertyUtils() {
	}

	@SuppressWarnings("unchecked")
	public static PropertyAccessor createAccessor(Object obj) {
		Assert.notNull(obj);
		if (obj instanceof Map) {
			return new MapPropertyAccessor((Map) obj);
		}
		return new BeanWrapperImpl(obj);
	}
	
	public static Object getProperty(Object bean, String name) {
		if (bean == null) {
			return null;
		}
		PropertyAccessor wrapper = createAccessor(bean);
		try {
			return wrapper.getPropertyValue(name);	
		}
		catch (NullValueInNestedPathException e) {
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static<T> T getProperty(Object bean, String name, 
			Class<T> requiredType) {
		
		Object value = getProperty(bean, name);
		if (value != null) {
			Assert.isInstanceOf(requiredType, value);
		}
		return (T) value;
	}

	public static String getPropertyAsString(Object bean, String name) {
		return convertToString(getProperty(bean, name));
	}

	public static void setProperty(Object bean, String name, Object value) {
		PropertyAccessor wrapper = createAccessor(bean);
		wrapper.setPropertyValue(name, value);
	}

	public static void setPropertyAsString(Object bean, String name, String s) {
		Class<?> type = getPropertyType(bean.getClass(), name);
		Object value = convert(s, type);
		setProperty(bean, name, value);
	}

	/**
	 * Returns a Map containing the bean's properties.
	 * @since 6.4
	 */
	public static Map<String, Object> getProperties(Object bean) {
		PropertyDescriptor[] pd = BeanUtils.getPropertyDescriptors(bean.getClass());
		HashMap<String, Object> properties = Generics.newHashMap();
		for (int i = 0; i < pd.length; i++) {
			Object value = ReflectionUtils.invokeMethod(pd[i].getReadMethod(), bean);
			properties.put(pd[i].getName(), value);
		}
		return properties;
	}

	/**
	 * Returns a Map containing the bean's properties.
	 * @since 6.4
	 */
	public static Map<String, Object> getProperties(Object bean, String[] propertyNames) {
		HashMap<String, Object> properties = Generics.newHashMap();
		for (int i = 0; i < propertyNames.length; i++) {
			String name = propertyNames[i];
			properties.put(name, getProperty(bean, name));
		}
		return properties;
	}

	/**
	 * Sets properties from the given Map.
	 * @see MutablePropertyValues#addPropertyValues(Map)
	 * @since 8.0
	 */
	public static void setProperties(Object bean, Map<String, ?> properties) {
		new BeanWrapperImpl(bean).setPropertyValues(new MutablePropertyValues(properties));
	}

	/**
	 * @since 6.4
	 */
	public static String evaluate(String expression, Object bean) {
		Matcher matcher = expressionPattern.matcher(expression);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			String property = matcher.group(1);
			Object value = getProperty(bean, property);
			matcher.appendReplacement(sb, convertToString(value));
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	public static<T> T convert(String s, Class<T> targetClass) {
		if (targetClass.equals(String.class)) {
			return (T) s;
		}
		PropertyEditor pe = registry.findEditor(targetClass);
		Assert.notNull(pe, "No PropertyEditor found for class: " + targetClass);
		synchronized (pe) {
			pe.setAsText(s);
			return (T) pe.getValue();
		}
	}

	/**
	 * @since 6.4
	 */
	public static String convertToString(Object value) {
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

    public static Class<?> getPropertyType(Class<?> clazz, String propertyPath) {
		int pos = PropertyAccessorUtils.getFirstNestedPropertySeparatorIndex(propertyPath);
		// Handle nested properties recursively.
		if (pos > -1) {
			String nestedProperty = propertyPath.substring(0, pos);
			String nestedPath = propertyPath.substring(pos + 1);
			Class<?> nestedClazz = getPropertyType(clazz, nestedProperty);
			return getPropertyType(nestedClazz, nestedPath);
		}
		else {
	        PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(clazz, propertyPath);
	        return pd != null ? pd.getPropertyType() : null;
		}
    }
    
    public static Class<?> getCollectionPropertyType(Class<?> clazz, String property) {
    	PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(clazz, property);
    	Method getter = pd.getReadMethod();
    	Assert.notNull(getter, "No getter for property " + property + " in class " + clazz);
    	return GenericCollectionTypeResolver.getCollectionReturnType(getter);
    }
    
    /**
     * Returns the (super-)class where the given property is declared.
     */
    public static Class<?> getDeclaringClass(Class<?> clazz,
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
        return ClassUtils.getUserClass(clazz);
    }

}

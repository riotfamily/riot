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
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.beans;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

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

	public static Object getProperty(Object bean, String name) {
		if (bean == null) {
			return null;
		}
		ProtectedBeanWrapper wrapper = new ProtectedBeanWrapper(bean);
		return wrapper.getPropertyValue(name);
	}

	public static String getPropertyAsString(Object bean, String name) {
		return convertToString(getProperty(bean, name));
	}

	public static void setProperty(Object bean, String name, Object value) {
		ProtectedBeanWrapper wrapper = new ProtectedBeanWrapper(bean);
		wrapper.setPropertyValue(name, value);
	}

	public static void setPropertyAsString(Object bean, String name, String s) {
		Class type = getPropertyType(bean.getClass(), name);
		Object value = convert(s, type);
		setProperty(bean, name, value);
	}

	/**
	 * Returns a Map containing the bean's properties.
	 * @since 6.4
	 */
	public static Map getProperties(Object bean) {
		PropertyDescriptor[] pd = BeanUtils.getPropertyDescriptors(bean.getClass());
		HashMap properties = new HashMap();
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
	public static Map getProperties(Object bean, String[] propertyNames) {
		HashMap properties = new HashMap();
		for (int i = 0; i < propertyNames.length; i++) {
			String name = propertyNames[i];
			properties.put(name, getProperty(bean, name));
		}
		return properties;
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

	/**
	 * @deprecated as of Riot 6.4, in favor of Spring's <code>BeanUtils.getPropertyDescriptor</code>.
	 */
	public static PropertyDescriptor getPropertyDescriptor(
			Class clazz, String property) {

        return BeanUtils.getPropertyDescriptor(clazz, property);
    }

    public static Class getPropertyType(Class clazz, String property) {
        PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(clazz, property);
        Assert.notNull(pd, "Property '" + property + "' not found in class " + clazz);
        return pd.getPropertyType();
    }

    /**
	 * @since 6.4
	 */
	public static Method findReadMethod(Class clazz, String property) {
		String methodName = "get" + StringUtils.capitalize(property);
		Method readMethod = BeanUtils.findDeclaredMethod(clazz, methodName, null);
		if (readMethod != null) {
			readMethod.setAccessible(true);
		}
		return readMethod;
	}

	/**
	 * @since 6.4
	 */
	public static Method findWriteMethod(Class clazz, String property) {
		String methodName = "set" + StringUtils.capitalize(property);
		Method writeMethod = BeanUtils.findDeclaredMethodWithMinimalParameters(
				clazz, methodName);

		if (writeMethod != null) {
			writeMethod.setAccessible(true);
		}
		return writeMethod;
	}

    /**
     * Returns the (super-)class where the given property is declared.
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
        return ClassUtils.getUserClass(clazz);
    }

    /**
     * Creates a new instance of the class specified by the given name.
     * The method uses {@link ClassUtils#forName(String)} and
     * {@link BeanUtils#instantiateClass(Class)} internally.
     * ClassNotFoundExceptions are caught and re-thrown as
     * {@link FatalBeanException}.
     */
    public static Object newInstance(String className) {
		try {
			Class clazz = ClassUtils.forName(className);
			return BeanUtils.instantiateClass(clazz);
		}
		catch (ClassNotFoundException e) {
			throw new FatalBeanException(e.getMessage(), e);
		}
    }

    /**
     * Partitions the given collection by inspecting the specified property
     * of the contained items.
     *
     * @param c The collection to partition
     * @param titleProperty The property to use for grouping
     * @return A list of {@link ObjectGroup ObjectGroups}
     */
    public static List partition(Collection c, String titleProperty) {
		ArrayList groups = new ArrayList();
		Iterator it = c.iterator();
		ObjectGroup group = null;
		while (it.hasNext()) {
			Object item = it.next();
			Object title = getProperty(item, titleProperty);
			if (group == null || (title != null
					&& !title.equals(group.getTitle()))) {

				group = new ObjectGroup(title, item);
				groups.add(group);
			}
			else {
				group.add(item);
			}
		}
		return groups;
	}

}

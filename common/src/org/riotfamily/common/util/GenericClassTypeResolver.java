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
package org.riotfamily.common.util;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GenericClassTypeResolver {

	private GenericClassTypeResolver() {
	}
	
	public static <T> Class<?> resolveTypeArgument(Class<?> clazz, Class<T> genericSuperClass) {
		return resolveTypeArguments(clazz, genericSuperClass).get(0);
	}
	
	public static <T> List<Class<?>> resolveTypeArguments(Class<?> clazz, Class<T> genericSuperClass) {
		
		if (genericSuperClass.isInterface()) {
			ParameterizedType pt = getInterfaceType(genericSuperClass, clazz);
			List<Class<?>> result = Generics.newArrayList();
			if (pt != null) {
				for (Type arg : pt.getActualTypeArguments()) {
					result.add(getClass(arg));
				}
			}
			return result;
		}
		
		Map<Type, Type> resolvedTypes = new HashMap<Type, Type>();
		Type type = clazz;
		
		while (!getClass(type).equals(genericSuperClass)) {
			if (type instanceof Class<?>) {
				type = ((Class<?>) type).getGenericSuperclass();
			}
			else {
				ParameterizedType parameterizedType = (ParameterizedType) type;
				Class<?> rawType = (Class<?>) parameterizedType.getRawType();

				Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
				TypeVariable<?>[] typeParameters = rawType.getTypeParameters();

				for (int i = 0; i < actualTypeArguments.length; i++) {
					resolvedTypes.put(typeParameters[i], actualTypeArguments[i]);
				}

				if (!rawType.equals(genericSuperClass)) {
					type = rawType.getGenericSuperclass();
				}
			}
		}

		Type[] actualTypeArguments;
		if (type instanceof Class<?>) {
			actualTypeArguments = ((Class<?>) type).getTypeParameters();
		}
		else {
			actualTypeArguments = ((ParameterizedType) type)
					.getActualTypeArguments();
		}
		
		List<Class<?>> typeArgumentsAsClasses = new ArrayList<Class<?>>();
		for (Type baseType : actualTypeArguments) {
			while (resolvedTypes.containsKey(baseType)) {
				baseType = resolvedTypes.get(baseType);
			}
			typeArgumentsAsClasses.add(getClass(baseType));
		}
		return typeArgumentsAsClasses;
	}
	
	private static Class<?> getClass(Type type) {
		if (type instanceof Class<?>) {
			return (Class<?>) type;
		}
		else if (type instanceof ParameterizedType) {
			return getClass(((ParameterizedType) type).getRawType());
		}
		else if (type instanceof GenericArrayType) {
			Type componentType = ((GenericArrayType) type)
					.getGenericComponentType();

			Class<?> componentClass = getClass(componentType);
			if (componentClass != null) {
				return Array.newInstance(componentClass, 0).getClass();
			}
		}
		return null;
	}
	
	private static ParameterizedType getInterfaceType(Class<?> interfaceClass, 
			Class<?> implementingClass) {
		
		for (Type genericInterface : implementingClass.getGenericInterfaces()) {
			Class<?> cls = getClass(genericInterface);
			if (cls.equals(interfaceClass) && genericInterface instanceof ParameterizedType) {
				return (ParameterizedType) genericInterface;
			}
			ParameterizedType pt = getInterfaceType(interfaceClass, cls);
			if (pt != null) {
				return pt;
			}
		}
		return null;
	}
}

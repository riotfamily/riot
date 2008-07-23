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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.website.cache;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.riotfamily.common.util.Generics;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

public class JpaIdResolver {
	
	private Map<Class<?>, IdAccessor> accessors = Generics.newHashMap();
	
	public Serializable getId(Object entity) {
		Class<?> clazz = entity.getClass();
		IdAccessor accessor = accessors.get(clazz);
		if (accessor == null) {
			accessor = getAccessor(clazz);
			accessors.put(clazz, accessor);
		}
		return accessor.getId(entity);
	}

	private IdAccessor getAccessor(Class<?> clazz) {
		clazz = AnnotationUtils.findAnnotationDeclaringClass(
				Entity.class, clazz);
		
		if (clazz != null) {
			for (Method method : clazz.getMethods()) {
				if (method.isAnnotationPresent(Id.class)
						|| method.isAnnotationPresent(EmbeddedId.class)) {
					
					return new MethodIdAccessor(method);
				}
			}
			for (Field field : clazz.getFields()) {
				if (field.isAnnotationPresent(Id.class)
						|| field.isAnnotationPresent(EmbeddedId.class)) {
					
					return new FieldIdAccessor(field);
				}		
			}
		}
		return new NoIdAccessor();
	}
	
	private interface IdAccessor {
		public Serializable getId(Object obj);
	}
	
	private static class FieldIdAccessor implements IdAccessor {

		private Field field;
		
		public FieldIdAccessor(Field field) {
			ReflectionUtils.makeAccessible(field);
			this.field = field;
		}

		public Serializable getId(Object obj) {
			try {
				return (Serializable) field.get(obj);
			} 
			catch (Exception ex) {
				ReflectionUtils.handleReflectionException(ex);
				return null;
			} 
		}
	}
	
	private static class MethodIdAccessor implements IdAccessor {

		private Method method;
		
		public MethodIdAccessor(Method method) {
			this.method = method;
		}

		public Serializable getId(Object obj) {
			return (Serializable) ReflectionUtils.invokeMethod(method, obj); 
		}
	}
	
	private static class NoIdAccessor implements IdAccessor {

		public Serializable getId(Object obj) {
			return null;
		}
	}
	
}

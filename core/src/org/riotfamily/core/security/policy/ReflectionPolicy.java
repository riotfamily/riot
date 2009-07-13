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
package org.riotfamily.core.security.policy;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.riotfamily.common.collection.TypeComparatorUtils;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.Generics;
import org.riotfamily.common.util.RiotLog;
import org.riotfamily.core.security.auth.RiotUser;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * AuthorizationPolicy that delegates permission checks to individual methods
 * via reflection. It looks for methods with the name of the action and either 
 * one or two parameters (the first one must be assignment-compatible with 
 * {@link RiotUser}) and an <code>int</code> return type. The action name is
 * uncapitalized and converted to camel-case.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class ReflectionPolicy implements AssertionPolicy {

	private RiotLog log = RiotLog.get(ReflectionPolicy.class);
	
	private Object delegate = this;
	
	private Map<ActionAndClass, Method> methods = new HashMap<ActionAndClass, Method>();
	
	private int order;
	
	public int getOrder() {
		return order;
	}
	
	public void setOrder(int order) {
		this.order = order;
	}
	
	public void setDelegate(Object delegate) {
		this.delegate = delegate;
		this.methods.clear();
	}
	
	public Permission getPermission(RiotUser user, String action, Object object) {
		Method method = getMethod(action, object, false);
		if (method != null) {
			return (Permission) invoke(method, user, action, object);
		}
		return Permission.ABSTAIN;
	}
	
	public void assertIsGranted(RiotUser user, String action, Object object) {
		Method method = getMethod(action, object, true);
		if (method != null) {
			invoke(method, user, action, object);
		}
		else if (getPermission(user, action, object) == Permission.DENIED) {
			throw new PermissionDeniedException(user, action, object, this);
		}
	}
	
	private Object invoke(Method method, RiotUser user, String action, Object object) {
		List<Object> args = Generics.newArrayList();
		if (object != null) {
			if (object.getClass().isArray()) {
				Object[] objects = (Object[]) object;
				for (Object obj : objects) {
					if (obj != null) {
						args.add(obj);
					}					
				}
			}
			else {
				args.add(object);
			}
		}
		if (method.getParameterTypes().length - args.size() == 2) {
			args.add(0, action);	
		}
		args.add(0, user);
		return ReflectionUtils.invokeMethod(method, delegate, args.toArray());
	}
	
	private Method getMethod(String action, Object object, boolean isVoid) {
		ActionAndClass aac = new ActionAndClass(action, object, isVoid);
		Method method = (Method) methods.get(aac);
		if (method == null) {
			if (!methods.containsKey(aac)) {
				method = findMethod(aac);
				methods.put(aac, method);
			}			
		}
		return method;
	}
	
	private Method findMethod(ActionAndClass aac) {
		Method bestMatch = null;
		Method[] methods = delegate.getClass().getMethods();
		if (aac.classes != null) {
			bestMatch = getBestMatch(methods, aac.action, aac.classes, aac.isVoid);
			if (bestMatch == null) {
				bestMatch = getBestMatch(methods, null, aac.classes, aac.isVoid);
			}
		}
		if (bestMatch == null) {
			bestMatch = getBestMatch(methods, aac.action, null, aac.isVoid);
		}
		
		if (bestMatch != null) {
			log.debug("Using " + bestMatch + " for " + aac);
		}
		else {
			log.debug("No method found for " + aac);
		}
		return bestMatch;
		
	}
	
	private Method getBestMatch(Method[] methods, String action, 
			Class<?>[] classes, boolean isVoid) {
		
		Method bestMatch = null;
		int smallestDiff = Integer.MAX_VALUE;
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if (signatureMatches(method, action, classes, isVoid)) {
				if (classes == null) {
					return method;
				}
				int diff = 0;
				for (int j = 0; j < classes.length; j++) {
					diff += TypeComparatorUtils.getTypeDifference(
							method.getParameterTypes()[j+1], classes[j]);
				}					
				if (diff < smallestDiff) {
					smallestDiff = diff;
					bestMatch = method;
				}
			}
		}
		return bestMatch;
	}
	
	
	private boolean signatureMatches(Method method, String action, 
			Class<?>[] classes, boolean isVoid) {
		
		if (!Modifier.isPublic(method.getModifiers())) {
			return false;
		}
		if (isVoid && method.getReturnType() != Void.TYPE) {
			return false;
		}
		if (!isVoid && !method.getReturnType().equals(Permission.class)) {
			return false;
		}
		Class<?>[] paramTypes = method.getParameterTypes();
		if (action != null) {
			if (method.getName().equals(action)) {
				return paramTypesMatch(paramTypes, classes, 1);
			}
		}
		else {
			if (method.getName().equals("getPermission")) {
				return paramTypesMatch(paramTypes, classes, 2) 
						&& paramTypes[1].equals(String.class);
			}
		}
		return false;
	}
	
	private boolean paramTypesMatch(Class<?>[] paramTypes, 
			Class<?>[] classes, int offset) {
		
		if (classes == null) {
			return paramTypes.length == offset;
		}
		if (paramTypes.length == classes.length + offset) {
			for (int i = 0; i < classes.length; i++) {
				if (!paramTypes[i + offset].isAssignableFrom(classes[i])) {
					return false;
				}						
			}
			return true;
		}
		return false;
	}
	
	private static class ActionAndClass {
		
		private String action;
		
		private Class<?>[] classes;
		
		private boolean isVoid;
		
		public ActionAndClass(String action, Object obj, boolean isVoid) {
			this.action = StringUtils.uncapitalize(FormatUtils.xmlToCamelCase(action));
			if (obj != null) {
				if (obj.getClass().isArray()) {
					Object[] objects = (Object[]) obj;
					List<Class<?>> tempClasses = Generics.newArrayList();					
					for (int i =0; i < objects.length; i++) {
						if (objects[i] != null) {
							tempClasses.add(ClassUtils.getUserClass(objects[i]));
						}
					}
					classes = tempClasses.toArray(new Class<?>[tempClasses.size()]);
					
				}
				else {
					classes = new Class<?>[] {ClassUtils.getUserClass(obj)};
				}
			}
			this.isVoid = isVoid;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			if (isVoid) {
				sb.append("void ");
			}
			sb.append(action);
			if (classes != null) {
				for (Class<?> clazz : classes) {
					sb.append(' ');
					sb.append(clazz.getName());
				}
			}
			return sb.toString();
		}
		
		public int hashCode() {
			return toString().hashCode();
		}
		
		public boolean equals(Object obj) {
			if (obj instanceof ActionAndClass) {
				ActionAndClass other = (ActionAndClass) obj;
				return ObjectUtils.nullSafeEquals(action, other.action)
						&& ObjectUtils.nullSafeEquals(classes, other.classes)
						&& isVoid == other.isVoid;
			}
			return false;
		}
	}

}

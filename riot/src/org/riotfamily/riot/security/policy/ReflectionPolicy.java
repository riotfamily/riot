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
package org.riotfamily.riot.security.policy;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.collection.TypeComparatorUtils;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.riot.security.auth.RiotUser;
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
public class ReflectionPolicy implements AuthorizationPolicy {

	private static final Log log = LogFactory.getLog(ReflectionPolicy.class);
	
	private Object delegate = this;
	
	private Map methods = new HashMap();
	
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
	
	public int checkPermission(RiotUser user, String action, Object object) {
		Method method = getMethod(action, object);
		if (method != null) {			
			Object[] args = new Object[] {user, object};
			Integer result = (Integer)ReflectionUtils.invokeMethod(method, delegate, args);
			return result.intValue();
		}
		return ACCESS_ABSTAIN;
	}
	
	private Method getMethod(String action, Object object) {
		ActionAndClass aac = new ActionAndClass(action, object);
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
		if (aac.clazz != null) {
			int smallestDiff = Integer.MAX_VALUE;
			Method[] methods = delegate.getClass().getMethods();
			for (int i = 0; i < methods.length; i++) {
				Method method = methods[i];
				if (signatureMatches(method, aac.action, aac.clazz)) {
					Class type = method.getParameterTypes()[1];
					int diff = TypeComparatorUtils.getTypeDifference(type, aac.clazz);
					if (diff < smallestDiff) {
						smallestDiff = diff;
						bestMatch = method;
					}
				}
			}
		}
		if (bestMatch == null) {
			bestMatch = findSingleParamMethod(aac.action);
		}
		if (bestMatch != null) {
			log.info("Using " + bestMatch + " for " + aac);
		}
		else {
			log.warn("No method found for " + aac);
		}
		return bestMatch;
		
	}
	
	
	private boolean signatureMatches(Method method, String action, Class type) {
		if (method.getName().equals(action) && Modifier.isPublic(method.getModifiers())) {
			Class[] types = method.getParameterTypes();
			if (types.length > 0 && RiotUser.class.isAssignableFrom(types[0])) {
				if (type == null) {
					return types.length == 1;
				}
				return types.length == 2 && types[1].isAssignableFrom(type);
			}
		}
		return false;
	}
	
	private Method findSingleParamMethod(String action) {
		Method[] methods = delegate.getClass().getMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if (signatureMatches(method, action, null)) {
				return method;
			}
		}
		return null;
	}	
	
	private static class ActionAndClass {
		
		private String action;
		
		private Class clazz;
		
		public ActionAndClass(String action, Object obj) {
			this.action = StringUtils.uncapitalize(FormatUtils.xmlToCamelCase(action));
			this.clazz = obj != null ? ClassUtils.getUserClass(obj) : null;
		}

		public String toString() {
			StringBuffer sb = new StringBuffer(action);
			if (clazz != null) {
				sb.append(' ');
				sb.append(clazz.getName());
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
						&& ObjectUtils.nullSafeEquals(clazz, other.clazz);
			}
			return false;
		}
	}

}

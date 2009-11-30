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
package org.riotfamily.core.security.policy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.Generics;
import org.riotfamily.core.security.auth.RiotUser;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * AuthorizationPolicy that delegates permission checks to individual methods
 * via reflection. It looks for methods with the name of the action and either 
 * one or two parameters (the first one must be assignment-compatible with 
 * {@link RiotUser}) and a <code>Permission</code> return type. The action name is
 * uncapitalized and converted to camel-case.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class ReflectionPolicy implements AuthorizationPolicy {

	private Object delegate = this;
	
	private Map<SignaturePattern, PermissionMethod> methods = Generics.newHashMap();
	
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
	
	public final Permission getPermission(RiotUser user, String action, Object object, Object context) {
		PermissionMethod method = getMethod(new SignaturePattern(action, object, context));
		if (method != null) {
			return method.invoke(delegate, user, action, object, context);
		}
		return Permission.ABSTAIN;
	}
		
	private PermissionMethod getMethod(SignaturePattern signature) {
		PermissionMethod method = methods.get(signature);
		if (method == null) {
			if (!methods.containsKey(signature)) {
				method = signature.find(delegate.getClass().getDeclaredMethods());
				methods.put(signature, method);
			}			
		}
		return method;
	}

	private enum Arguments {
		V1(0 ,1, 1),
		V2(1 ,1, 1),
		V3(0 ,1, 0),
		V4(0 ,0, 1),
		V5(1 ,1, 0),
		V6(1 ,0, 1);
		
		private boolean action;
		private boolean object;
		private boolean context;
		
		Arguments(int action, int object, int context) {
			this.action = action > 0;
			this.object = object > 0;
			this.context = context > 0;
		}
		
		public Object[] buildArgs(RiotUser user, 
				String action, Object object, Object context) {
			
			List<Object> args = Generics.newArrayList();
			args.add(user);
			if (this.action) {
				args.add(action);
			}
			if (this.object) {
				args.add(object);
			}
			if (this.context) {
				args.add(context);
			}
			return args.toArray();
		}
	}
	
	private static class PermissionMethod {
		
		private Method method;
		
		private Arguments arguments;

		public PermissionMethod(Method method, Arguments arguments) {
			this.method = method;
			this.arguments = arguments;
		}
		
		public Permission invoke(Object delegate, RiotUser user, 
				String action, Object object, Object context) {

			Object[] args = arguments.buildArgs(user, action, object, context);
			try {
				return (Permission) method.invoke(delegate, args);
			}
			catch (IllegalArgumentException e) {
				throw new RuntimeException(
						String.format("Failed to invoke %s with arguments %s", 
						method.toString(), StringUtils.arrayToCommaDelimitedString(args)), e);
			}
			catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
		
	}
	private static class SignaturePattern {
		
		private String action;
		
		private Class<?> objectClass;
		
		private Class<?> contextClass;
		
		public SignaturePattern(String action, Object obj, Object context) {
			this.action = StringUtils.uncapitalize(FormatUtils.xmlToCamelCase(action));
			if (obj != null) {
				objectClass = Hibernate.getClass(obj);
			}
			if (context != null) {
				contextClass = context.getClass();
			}
		}
		
		public PermissionMethod find(Method[] methods) {
			for (Arguments arguments : Arguments.values()) {
				Method method = find(methods, arguments);
				if (method != null) {
					return new PermissionMethod(method, arguments);
				}
			}
			return null;
		}
		
		private Method find(Method[] methods, Arguments arguments) {
			Method match = null;
			for (int i = 0; i < methods.length; i++) {
				Method method = methods[i];
				if (matches(method, arguments)) {
					Assert.isNull(match, "Found ambigious signatures: " + method + " and " + match);
					match = method;
				}
			}
			return match;
		}
		
		private boolean matches(Method method, Arguments arguments) {
			if (!Modifier.isPublic(method.getModifiers())) {
				return false;
			}
			if (!method.getReturnType().equals(Permission.class)) {
				return false;
			}
			Class<?>[] paramTypes = method.getParameterTypes();
			if (arguments.action) {
				if (method.getName().equals("getPermission")) {
					return paramTypesMatch(paramTypes, 2, arguments) 
							&& paramTypes[1].equals(String.class);
				}				
			}
			else {
				if (method.getName().equals(action)) {
					return paramTypesMatch(paramTypes, 1, arguments);
				}
			}
			return false;
		}
		
		private boolean paramTypesMatch(Class<?>[] paramTypes, int offset, Arguments arguments) {
			int i = offset;
			if (arguments.object && objectClass != null) {
				if (paramTypes.length <= i || !paramTypes[i].isAssignableFrom(objectClass)) {
					return false;
				}
				i++;
			}
			if (arguments.context && contextClass != null) {
				if (paramTypes.length <= i || !paramTypes[i].isAssignableFrom(contextClass)) {
					return false;
				}
				i++;
			}
			return paramTypes.length == i;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(action);
			if (objectClass != null) {
				sb.append(' ');
				sb.append(objectClass.getName());
			}
			if (contextClass != null) {
				sb.append(' ');
				sb.append(contextClass.getName());
			}
			return sb.toString();
		}
		
		@Override
		public int hashCode() {
			return toString().hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof SignaturePattern) {
				SignaturePattern other = (SignaturePattern) obj;
				return ObjectUtils.nullSafeEquals(action, other.action)
						&& ObjectUtils.nullSafeEquals(objectClass, other.objectClass)
						&& ObjectUtils.nullSafeEquals(contextClass, other.contextClass);
			}
			return false;
		}
	}

}

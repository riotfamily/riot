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
package org.riotfamily.forms2.base;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;

import org.riotfamily.common.util.ExceptionUtils;
import org.riotfamily.forms2.client.FormResource;
import org.riotfamily.forms2.value.Value;
import org.springframework.util.ReflectionUtils;

/**
 * Base class for all form elements. Elements themselves are stateless and must
 * implement the {@link #createState(Value)} method. 
 */
public abstract class Element implements Serializable {
	
	/**
	 * Creates a new state instance for the given value. After 
	 * calling {@link #createState(Value)}, the init() method is invoked,
	 * passing the specified parent.
	 */
	ElementState createAndInitState(ElementState parent, Value value) {
		FormState formState = parent != null ? parent.getFormState() : null;
		ElementState state = createState();
		state.init(parent, formState, value);
		return state;
	}
	
	public final ElementState createState(ElementState parent, Value value) {
		return createAndInitState(parent, value);
	}
	
	/**
	 * Creates an empty ElementState. The default implementation creates a new
	 * state via reflection, using the following convention:
	 * <p>
	 * It goes up the class hierarchy until it finds a class that defines 
	 * an inner class which is a subclass of {@link ElementState}.
	 * <p>
	 * The inner class must have a <b>public default constructor</b> and must
	 * be declared as <b>static class</b>, so that it doesn't have a reference
	 * to the enclosing non-serializable element.
	 */
	protected ElementState createState() {
		try {
			for(Class<?> c = getClass(); c != null; c = c.getSuperclass()) {
				for (Class<?> innerClass : c.getDeclaredClasses()) {
					if (ElementState.class.isAssignableFrom(innerClass)) {
						return innerClass.asSubclass(ElementState.class).getConstructor(c).newInstance(this);
					}
				}
			}
			throw new IllegalStateException(getClass() + " does not declare a State class");
		}
		catch (Exception e) {
			throw ExceptionUtils.wrapReflectionException(e);
		}
	}
	
	
	void init(Element original) {
		try {
			for (Class<?> c = getClass(); c != null; c = c.getSuperclass()) {
				for (Field field : c.getDeclaredFields()) {
					if (Modifier.isTransient(field.getModifiers())) {
						ReflectionUtils.makeAccessible(field);
						Object thisValue = field.get(this);
						if (thisValue == null) {
							Object originalValue = field.get(original);
							field.set(this, originalValue);
						}
					}
				}
			}
		}
		catch (Exception e) {
			throw ExceptionUtils.wrapReflectionException(e);
		}
	}
	
	/**
	 * Subclasses may overwrite this method if the element depends on external
	 * resources like CSS or JavaScript files. The default implementation calls
	 * {@link #getResource()}.
	 */
	public Collection<FormResource> getResources() {
		FormResource res = getResource();
		return res != null ? Collections.singleton(res) : null;
	}

	/**
	 * The default implementation returns <code>null</code>.
	 * @see #getResources()
	 */
	protected FormResource getResource() {
		return null;
	}
	
	public Collection<Element> getChildElements() {
		return null;
	}
	
}

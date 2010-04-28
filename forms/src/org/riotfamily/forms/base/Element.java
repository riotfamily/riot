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
package org.riotfamily.forms.base;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.riotfamily.common.util.ExceptionUtils;
import org.riotfamily.common.util.Generics;
import org.riotfamily.forms.client.FormResource;
import org.riotfamily.forms.client.Html;
import org.riotfamily.forms.value.TypeInfo;
import org.riotfamily.forms.value.Value;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * Base class for all form elements. Elements themselves are stateless and must
 * (by convention) declare an inner class that extends {@link Element.State}.
 */
public abstract class Element implements Serializable {
	
	private boolean verified;
	
	/**
	 * Creates a new state instance for the given value. After 
	 * calling {@link #createState()}, the init() method is invoked,
	 * passing the specified parent.
	 */
	final Element.State createAndInitState(Element.State parent) {
		verifyParentImplementation(parent);
		FormState formState = parent.getFormState();
		Element.State state = createState();
		state.init(parent, formState);
		return state;
	}
	
	/**
	 * Verifies that the getChildElements() method the parent element is 
	 * implemented correctly.
	 */
	private void verifyParentImplementation(State parent) {
		if (!verified) {
			Collection<Element> childElements = parent.getElement().getChildElements();
			String message = parent.getElement().getClass()  
					+ ".getChildElements() must return a Collection containing " + this;
			
			Assert.notNull(childElements, message);
			Assert.state(childElements.contains(this), message);
			verified = true;
		}
	}

	public final Element.State createState(Element.State parent) {
		return createAndInitState(parent);
	}
	
	/**
	 * Creates an empty Element.State. The default implementation creates a new
	 * state via reflection. It goes up the class hierarchy until it finds a 
	 * class that defines an inner class which is a subclass of 
	 * {@link Element.State}.
	 */
	protected Element.State createState() {
		try {
			for(Class<?> c = getClass(); c != null; c = c.getSuperclass()) {
				for (Class<?> innerClass : c.getDeclaredClasses()) {
					if (Element.State.class.isAssignableFrom(innerClass)) {
						return innerClass.asSubclass(Element.State.class).getDeclaredConstructor(c).newInstance(this);
					}
				}
			}
			throw new IllegalStateException(getClass() + " does not declare a State class");
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
	
	public abstract class State implements Serializable {

		private String id;
		
		private boolean enabled = true;
			
		private Element.State parent;
		
		private FormState formState;

		private List<StateEventHandler> handlers;

		private Integer hashCode;
		
		/**
		 * Empty default constructor. 
		 */
		public State() {
		}
		
		/**
		 * Constructor that allows the FormState subclass to assign an id to itself.
		 */
		State(String id) {
			this.id = id;
		}
		
		Element getElement() {
			return Element.this;
		}
		
		/**
		 * Returns the FormState that was set via the {@link #init} method.
		 */
		public final FormState getFormState() {
			return getFormStateInternal();
		}

		/**
		 * Returns the parent state that was set via the {@link #init} method.
		 */
		public final Element.State getParent() {
			return parent;
		}
		
		public TypeInfo getTypeInfo() {
			return parent.getTypeInfo();
		}
		
		/**
		 * Returns the state's unique id. The id is assigned during {@link #init} by
		 * calling <code>parent.register(this)</code>.
		 */
		public final String id() {
			return id;
		}

		/**
		 * Returns whether the element is enabled, i.e. accepts user input.
		 */
		public final boolean isEnabled() {
			return enabled && (parent == null || parent.isEnabled());
		}
		
		/**
		 * Enables or disables the element. Disabling an element also implicitly 
		 * disables its decendants, as {@link #isEnabled()} takes the parent state
		 * into account. 
		 */
		public final void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
		
		/**
		 * Renders the element by calling {@link #renderElement(Html, Element)} and
		 * wraps the result into a DIV that identifies this state.
		 */
		public final void render(Html html) {
			renderElement(wrap(html));
		}
			
		/**
		 * Creates a DIV that is used to wrap the actual element. The DIV has this
		 * state's {@link #id() id} and a CSS class called <code>state</code>, 
		 * which is used by the JavaScript to obtain the <code<stateId</code> of 
		 * nested DOM elements.
		 * <p>
		 * Additionally the {@link ClassUtils#getShortName(Class) short name} of 
		 * the element's Java class is assigned as CSS class.
		 */
		Html wrap(Html html) {
			return html.div("state").id(id())
					.addClass(ClassUtils.getShortName(getClass().getDeclaringClass()));
		}
		
		/**
		 * Convenience method for subclasses that can be used as shortcut for
		 * <code>getFormState().newHtml()</code>.
		 */
		protected Html newHtml() {
			return formState.newHtml();
		}

		/**
		 * Called by {@link Element#createAndInitState} to set the elementId, 
		 * parent, formState and to assign a unique id. Invokes 
		 * {@link #onInit()} to allow subclasses to perform additional 
		 * initialization tasks.
		 */
		final void init(Element.State parent, FormState formState) {
			this.parent = parent;
			this.formState = formState;
			if (parent != null) {
				id = parent.register(this);
			}
			onInit();
		}

		/**
		 * Callback method that can be overwritten by subclasses to perform 
		 * initialization tasks.
		 * <p>
		 * At this point it is guaranteed that the {@link #id() id}, 
		 * the {@link #getParent() parent}, the {@link #getElementId() elementId}
		 * and the {@link #getFormState() formState} have been set.
		 * <p>
		 * <b>Important:</b> Implementations that need to created nested states 
		 * must do this in this method, not inside their constructor.
		 */
		protected void onInit() {
		}
		
		public abstract void setValue(Object value);
		
		public void populate(Value value) {
			value.set(getValue());
		}
		
		public Object getValue() {
			return null;
		}
		
		protected abstract void renderElement(Html html);
		
		@SuppressWarnings("unchecked")
		protected <T> T getOrCreate(Value value, Class<T> requiredType, Class<? extends T> defaultType) {
			if (value.get() == null) {
				value.set(instanciateType(requiredType, defaultType));
			}
			return (T) value.get();
		}
		
		@SuppressWarnings("unchecked")
		private <T> T instanciateType(Class<T> requiredType, Class<? extends T> defaultType) {
			Class<?> type = getTypeInfo().getType();
			if (canInstanciate(type)) {
				return (T) instanciate(type);
			}
			if (defaultType == null) {
				defaultType = requiredType;
			}
			return instanciate(defaultType);
		}
		
		private <T> T instanciate(Class<T> type) {
			try {
				return type.newInstance();
			}
			catch (Exception e) {
				throw ExceptionUtils.wrapReflectionException(e);
			}
		}
		
		private boolean canInstanciate(Class<?> type) {
			if (type != null && !type.isInterface() && !Modifier.isAbstract(type.getModifiers())) {
				for (Constructor<?> c : type.getConstructors()) {
					if (c.getParameterTypes().length == 0 && Modifier.isPublic(c.getModifiers())) {
						return true;
					}
				}
			}
			return false;
		}
		
		/**
		 * Returns a newly created unique id by delegating the call to the
		 * parent state.
		 * @see FormState#register(Element.State)
		 */
		String register(Element.State state) {
			return parent.register(state);
		}

		/**
		 * Invoked internally by the final {@link #getFormState()} method. The 
		 * method has package-visibility to allow {@link FormState#getFormState()} 
		 * to return a reference to itself.
		 */
		FormState getFormStateInternal() {
			return formState;
		}
		
		public void addStateEventHandler(StateEventHandler handler) {
			if (handlers == null) {
				handlers = Generics.newArrayList();
			}
			handlers.add(handler);
		}
		
		public void handleStateEvent(StateEvent event) {
			if (handlers != null) {
				for (StateEventHandler handler : handlers) {
					handler.handle(event);
				}
			}
			if (!event.isStopped() && parent != null) {
				parent.handleStateEvent(event);
			}
		}
		
		@Override
		public int hashCode() {
			if (hashCode == null) {
				hashCode = id != null ? id.hashCode() : 0;
			}
			return hashCode;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (getClass().equals(obj.getClass())) {
				return id != null && id.equals(getClass().cast(obj).id);
			}
			return false;
		}
	}

	
}

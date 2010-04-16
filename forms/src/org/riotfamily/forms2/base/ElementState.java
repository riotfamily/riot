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

import org.riotfamily.forms2.client.Html;
import org.riotfamily.forms2.value.Value;
import org.springframework.util.ClassUtils;


public abstract class ElementState implements Serializable {

	private String id;
	
	private boolean enabled = true;
		
	private ElementState parent;
	
	private FormState formState;
	
	/**
	 * Empty default constructor. 
	 */
	public ElementState() {
	}
	
	/**
	 * Constructor that allows the FormState subclass to assign an id to itself.
	 */
	ElementState(String id) {
		this.id = id;
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
	public final ElementState getParent() {
		return parent;
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
	protected void onInit(Value value) {
	}
	
	protected abstract void renderElement(Html html);
		
	public abstract void populate(Value value);
	
	/**
	 * Called by {@link Element#createAndInitState} to set the elementId, 
	 * parent, formState and to assign a unique id. Invokes 
	 * {@link #onInit(Element, Value)} to allow subclasses to perform 
	 * additional initialization tasks.
	 */
	final void init(ElementState parent, FormState formState, Value value) {
		this.parent = parent;
		this.formState = formState;
		if (parent != null) {
			id = parent.register(this);
		}
		onInit(value);
	}
	
	/**
	 * Returns a newly created unique id by delegating the call to the parent 
	 * state.
	 * 
	 * @see #init(Element, ElementState, FormState, Value)
	 * @see FormState#register(ElementState)
	 */
	String register(ElementState state) {
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
}

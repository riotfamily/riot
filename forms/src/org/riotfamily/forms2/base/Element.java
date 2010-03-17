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

import java.util.Collection;

import org.riotfamily.forms2.client.FormResource;
import org.riotfamily.forms2.value.Value;

/**
 * Base class for all form elements. Elements themselves are stateless and must
 * implement the {@link #createState(Value)} method. 
 */
public abstract class Element {

	private String id;
	
	private Element parent;

	private FormElement root;
	
	/**
	 * Invoked by container elements to establish a parent/child relation.
	 */
	public final void setParent(Element parent) {
		this.parent = parent;
	}

	/**
	 * Invoked by {@link FormElement#register(Element)} to assign an id.
	 */
	final void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Invoked by {@link FormElement#register(Element)}.
	 */
	final void setRoot(FormElement root) {
		this.root = root;
	}

	public FormElement getRoot() {
		if (root == null) {
			register(this);
		}
		return root;
	}
	
	/**
	 * Registers the given element with the parent. As a result, 
	 * {@link #setId(String)} and {@link #setRoot(FormElement)} will be called
	 * by {@link FormElement#register(Element)}.
	 * <p>
	 * The method is part of the internal API and should not be overwritten
	 * by subclasses other than {@link FormElement}. 
	 */
	protected void register(Element element) {
		parent.register(element);
	}
	
	/**
	 * Returns the element's id. If no id has been assigned yet, 
	 * {@link #register(Element) register(this)} is invoked.
	 * @return
	 */
	public final String getId() {
		if (id == null) {
			register(this);
		}
		return id;
	}
	
	/**
	 * Creates a new state instance for the given value. After 
	 * calling {@link #createState(Value)}, the init() method is invoked,
	 * passing the specified parent.
	 */
	public final ElementState createState(ElementState parent, Value value) {
		ElementState state = createState(value);
		state.init(this, parent, parent.getFormState(), value);
		return state;
	}
	
	/**
	 * Subclasses must implement this method and return a state for the given
	 * value.
	 */
	protected abstract ElementState createState(Value value);
	
	/**
	 * Subclasses may overwrite this method if the element depends on external
	 * resources like CSS or JavaScript files.
	 */
	public Collection<FormResource> getResources() {
		return null;
	}
	
}

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
package org.riotfamily.forms;

import java.util.List;

import org.riotfamily.forms.ui.Dimension;


/**
 * Composite element that notifies the form whenever an element is added or 
 * removed. This way elements can benefit from the framework's AJAX support 
 * without needing to know anything about. 
 * Refer to the {@link org.riotfamily.forms.element.collection.ListEditor} implementation
 * for an example.
 */
public class Container extends CompositeElement implements ContainerElement {

	private Dimension componentPadding = new Dimension();
	
	/**
	 * Creates an empty container.
	 */
	public Container() {
	}
	
	public Container(List<? extends Element> components) {
		super(components);
	}

	public List<Element> getElements() {
		return getComponents();
	}

	public void addElement(Element element) {
		addComponent(element);
		if (getFormListener() != null) {
			getFormListener().elementAdded(element);
		}
	}

	public void setComponentPadding(Dimension componentPadding) {
		this.componentPadding = componentPadding;
	}
	
	@Override
	protected Dimension getComponentPadding(Element component) {
		return componentPadding;
	}
	
	/**
	 * Removes the given element from the container.
	 */
	public void removeElement(Element element) {
		removeComponent(element);
		getForm().unregisterElement(element);
		if (getFormListener() != null) {
			getFormListener().elementRemoved(element);
		}
	}
	
}
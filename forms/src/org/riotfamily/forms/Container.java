package org.riotfamily.forms;

import java.util.List;


/**
 * Composite element that notifies the form whenever an element is added or 
 * removed. This way elements can benefit from the framework's AJAX support 
 * without needing to know anything about. 
 * Refer to the {@link org.riotfamily.forms.element.collection.ListEditor} implementation
 * for an example.
 */
public class Container extends CompositeElement implements ContainerElement {

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
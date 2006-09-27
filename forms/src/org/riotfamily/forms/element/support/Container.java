package org.riotfamily.forms.element.support;

import java.io.PrintWriter;
import java.util.List;

import org.riotfamily.common.markup.Html;
import org.riotfamily.common.markup.TagWriter;
import org.riotfamily.forms.Element;


/**
 * Composite element that notifys the form whenever an element is added or 
 * removed. This way elements can benefit from the framework's AJAX support 
 * without needing to know anything about. 
 * Refer to the {@link org.riotfamily.forms.element.core.ListEditor} implementation
 * for an example.
 */
public class Container extends CompositeElement {

	/**
	 * Creates an empty container.
	 */
	public Container() {
	}

	public List getElements() {
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

	/**
	 * Renders the container's components surrounded by a <tt>span</tt> tag 
	 * with the id of the container.
	 */
	public void renderInternal(PrintWriter writer) {
		TagWriter div = new TagWriter(writer);
		div.start(Html.DIV).attribute(Html.COMMON_ID, getId()).body();
		renderComponents(writer);
		div.end();
	}
	
}
package org.riotfamily.forms.element.core;

import java.util.List;

import org.riotfamily.forms.Element;
import org.riotfamily.forms.element.ContainerElement;
import org.riotfamily.forms.element.support.Container;
import org.riotfamily.forms.element.support.TemplateElement;
import org.riotfamily.forms.i18n.MessageUtils;


/**
 * Element that visually groups other elements.
 */
public class ElementGroup extends TemplateElement implements ContainerElement {

	private Container container = new Container();

	private String labelKey;
	
	private boolean labelItems = true;

	public ElementGroup() {
		super("group");
		addComponent("elements", container);
	}

	public List getElements() {
		return container.getElements();
	}
	
	public void addElement(Element element) {
		container.addElement(element);
	}
	
	public void removeElement(Element element) {
		container.removeElement(element);
	}

	public void setLabelKey(String key) {
		labelKey = key;
	}	

	public boolean isLabelItems() {
		return labelItems;
	}

	public void setLabelItems(boolean labelItems) {
		this.labelItems = labelItems;
	}

	public String getLabel() {
		if (labelKey == null) {
			return "";
		}
		return MessageUtils.getMessage(this, labelKey);
	}
}

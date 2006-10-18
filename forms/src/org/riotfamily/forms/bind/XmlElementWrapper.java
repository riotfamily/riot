package org.riotfamily.forms.bind;

import org.riotfamily.common.beans.ObjectWrapper;
import org.riotfamily.common.xml.XmlUtils;
import org.springframework.beans.AbstractPropertyAccessor;
import org.springframework.beans.BeansException;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XmlElementWrapper extends AbstractPropertyAccessor 
		implements ObjectWrapper {

	private static final String ATTRIBUTE_PREFIX = "@";
	
	private static final String TEXT_VALUE = "text()";
	
	public Element element;

	private String elementName;
	
	
	public XmlElementWrapper(String elementName) {
		this.elementName = elementName;
	}

	public Class getWrappedClass() {
		return Element.class;
	}
	
	public void setWrappedInstance(Object object) {
		if (object != null) {
			element = (Element) object;
		}
		else {
			element = XmlUtils.createDocument().createElement(elementName);
		}
	}
	
	public Object getWrappedInstance() {
		return element;
	}
	
	public Object getPropertyValue(String name) throws BeansException {
		if (element == null) {
			return null;
		}
		if (name.startsWith(ATTRIBUTE_PREFIX)) {
			return element.getAttribute(name.substring(1));
		}
		if (name.equals(TEXT_VALUE)) {
			return DomUtils.getTextValue(element);
		}
		Element child = XmlUtils.getFirstChildByTagName(element, name);
		return child != null ? DomUtils.getTextValue(child) : null; 
	}

	public void setPropertyValue(String name, Object value) throws BeansException {
		if (element == null) {
			element = XmlUtils.createDocument().createElement(elementName);
		}
		if (name.startsWith(ATTRIBUTE_PREFIX)) {
			if (value != null) {
				element.setAttribute(name.substring(1), value.toString());
			}
			else {
				element.removeAttribute(name);
			}
		}
		else if (name.equals(TEXT_VALUE)) {
			XmlUtils.setTextValue(element, value.toString()); 
		}
		if (value instanceof Element) {
			Element child = XmlUtils.getFirstChildByTagName(element, name);
			XmlUtils.removeNode(child);
			child = (Element) value;
			Node node = element.getOwnerDocument().importNode(child, true);
			element.appendChild(node);
		}
		else {
			Element child = XmlUtils.getFirstChildByTagName(element, name);
			if (value != null) {
				if (child == null) {
					child = element.getOwnerDocument().createElement(name);
					element.appendChild(child);
				}
				XmlUtils.setTextValue(child, (String) value);
			}
			else {
				XmlUtils.removeNode(child);
			}
		}
	}
	
	public boolean isReadableProperty(String propertyName) throws BeansException {
		return true;
	}

	public boolean isWritableProperty(String propertyName) throws BeansException {
		return true;
	}
	
}

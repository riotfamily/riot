package org.riotfamily.common.xml;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.riotfamily.common.util.FormatUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.Assert;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Utility class that provides methods to create and populate beans from
 * DOM elements. 
 */
public class DigesterUtils {
		
	private static final String PROPERTY_NAME = "name";
	
	private static final String PROPERTY_VALUE = "value";
	
	private static final String PROPERTY_REF = "ref";

	public static String getLocalName(Node node) {
		String name = node.getLocalName();
		return name != null ? name : node.getNodeName();
	}
	
	public static List getChildElements(Element ele) {
		NodeList nl = ele.getChildNodes();
		List childEles = new LinkedList();
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			if (node instanceof Element) {
				childEles.add(node);
			}
		}
		return childEles;
	}
		
	public static List getChildElementsByRegex(Element ele, String pattern) {
		NodeList nl = ele.getChildNodes();
		List childEles = new LinkedList();
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			if (node instanceof Element && getLocalName(node).matches(pattern)) {
				childEles.add(node);
			}
		}
		return childEles;
	}
	
	public static Element getFirstChildElement(Element parent) {
		Node child = parent.getFirstChild();
		while (child != null) {
			if (child instanceof Element) {
				return (Element) child;
			}
			child = child.getNextSibling();
		}
		return null;
	}
		
	public static Element getFirstChildByTagName(Element ele, String tagName) {
		NodeList nl = ele.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			if (tagName.equals(getLocalName(node))) {
				return (Element) node;
			}
		}
		return null;
	}
	
	public static Element getFirstChildByRegex(Element ele, String pattern) {
		NodeList nl = ele.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			if (node instanceof Element && getLocalName(node).matches(pattern)) {
				return (Element) node;
			}
		}
		return null;
	}
	
	public static String getAttribute(Element ele, String name) {
		Assert.hasText(name, "An attribute name must be specified");
		Attr attr = ele.getAttributeNode(name);
		return attr != null ? attr.getNodeValue() : null;
	}
	
	public static int getIntAttribute(Element ele, String name, 
			int defaultValue) {
		
		String s = getAttribute(ele, name);
		if (s != null) {
			try {
				return Integer.parseInt(s);
			}
			catch (NumberFormatException e) {
			}
		}
		return defaultValue;
	}
	
	public static boolean getBooleanAttribute(Element ele, String name) {
		String s = getAttribute(ele, name);
		return Boolean.valueOf(s).booleanValue();
	}
	
	public static void populate(Object bean, NamedNodeMap attributes) {
		if (attributes != null) {
			BeanWrapper beanWrapper = new BeanWrapperImpl(bean);
			int length = attributes.getLength();
			for (int i = 0; i < length; i++) {
				Attr attr = (Attr) attributes.item(i);
				String property = FormatUtils.xmlToCamelCase(attr.getName());
				beanWrapper.setPropertyValue(property, attr.getValue());
			}
		}
	}
	
	public static void populate(Object bean, Element element, 
			String[] attributeNames) {
		
		populate(bean, element, attributeNames, null);
	}
	
	public static void populate(Object bean, Element element, 
			String[] attributeNames, BeanFactory beanFactory) {
		
		BeanWrapper beanWrapper = new BeanWrapperImpl(bean);
		for (int i = 0; i < attributeNames.length; i++) {
			setProperty(beanWrapper, attributeNames[i], element, beanFactory);
		}
	}
	
	private static void setProperty(BeanWrapper beanWrapper, String attr, 
			Element element, BeanFactory beanFactory) {
		
		String property = null;
		int i = attr.indexOf('=');
		if (i != -1) {
			property = attr.substring(0, i);
			attr = attr.substring(i + 1);
		}
		
		boolean beanRef = attr.charAt(0) == '@';
		if (beanRef) {
			attr = attr.substring(1);
		}
		
		if (property == null) {
			property = FormatUtils.xmlToCamelCase(attr);
		}
		
		String value = element.getAttribute(attr); 
		if (value.length() > 0) {
			if (beanRef) {
				Object object = beanFactory.getBean(value);
				beanWrapper.setPropertyValue(property, object);
			}
			else {
				beanWrapper.setPropertyValue(property, value);
			}
		}
	}
	
	public static void populate(Object bean, List elements, 
			BeanFactory beanFactory) {
		
		BeanWrapper beanWrapper = new BeanWrapperImpl(bean);
		Iterator it = elements.iterator();
		while (it.hasNext()) {
			Element ele = (Element) it.next();
			String name = getAttribute(ele, PROPERTY_NAME);
			Object value = getAttribute(ele, PROPERTY_VALUE);
			if (value == null) {
				String beanId = getAttribute(ele, PROPERTY_REF);
				if (beanId != null) {
					Assert.notNull(beanFactory, 
							"A BeanFactory must be supplied in order to set " +
							"properties by reference.");
							
					value = beanFactory.getBean(beanId);
				}
			}
			beanWrapper.setPropertyValue(name, value);
		}
	}
}

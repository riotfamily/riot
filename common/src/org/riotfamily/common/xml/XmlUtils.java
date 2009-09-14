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
package org.riotfamily.common.xml;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.riotfamily.common.util.FormatUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.Assert;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Utility class that provides methods to create and populate beans from
 * DOM elements.
 */
public class XmlUtils {

	private static final String PROPERTY_NAME = "name";

	private static final String PROPERTY_VALUE = "value";

	private static final String PROPERTY_REF = "ref";

	public static String getLocalName(Node node) {
		String name = node.getLocalName();
		return name != null ? name : node.getNodeName();
	}

	public static List<Element> getChildElements(Element ele) {
		NodeList nl = ele.getChildNodes();
		List<Element> childEles = new LinkedList<Element>();
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			if (node instanceof Element) {
				childEles.add((Element) node);
			}
		}
		return childEles;
	}
	
	public static List<Element> getChildElementsByTagName(Element ele, String name) {
		NodeList nl = ele.getChildNodes();
		List<Element> childEles = new LinkedList<Element>();
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			if (DomUtils.nodeNameEquals(node, name)) {
				childEles.add((Element) node);
			}
		}
		return childEles;
	}

	public static List<Element> getChildElementsByRegex(Element ele, String pattern) {
		NodeList nl = ele.getChildNodes();
		List<Element> childEles = new LinkedList<Element>();
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			if (node instanceof Element && getLocalName(node).matches(pattern)) {
				childEles.add((Element) node);
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
	
	public static Element getNextSiblingElement(Node node) {
		node = node.getNextSibling();
		while (node != null) {
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				return (Element) node;
			}
			node = node.getNextSibling();
		}
		return null;
	}

	public static String getAttribute(Element ele, String name) {
		Assert.hasText(name, "An attribute name must be specified");
		Attr attr = ele.getAttributeNode(name);
		return attr != null ? attr.getNodeValue() : null;
	}

	public static Integer getIntegerAttribute(Element ele, String name) {
		String s = getAttribute(ele, name);
		if (s != null) {
			try {
				return new Integer(s);
			}
			catch (NumberFormatException e) {
			}
		}
		return null;
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
		BeanWrapper beanWrapper = new BeanWrapperImpl(bean);
		beanWrapper.setPropertyValues(getPropertyValues(attributes));
	}
	
	public static PropertyValues getPropertyValues(NamedNodeMap attributes) {
		MutablePropertyValues pvs = new MutablePropertyValues();
		if (attributes != null) {
			int length = attributes.getLength();
			for (int i = 0; i < length; i++) {
				Attr attr = (Attr) attributes.item(i);
				String property = FormatUtils.xmlToCamelCase(attr.getName());
				pvs.addPropertyValue(property, attr.getValue());
			}
		}
		return pvs;
	}

	public static void populate(Object bean, Element element,
			String[] attributeNames) {

		populate(bean, element, attributeNames, null);
	}

	public static void populate(Object bean, Element element,
			String[] attributeNames, BeanFactory beanFactory) {

		BeanWrapper beanWrapper = new BeanWrapperImpl(bean);
		beanWrapper.setPropertyValues(getPropertyValues(
				element, attributeNames, beanFactory));
	}
	
	public static PropertyValues getPropertyValues(Element element,
			String[] attributeNames, BeanFactory beanFactory) {

		MutablePropertyValues pvs = new MutablePropertyValues();
		for (int i = 0; i < attributeNames.length; i++) {
			PropertyValue pv = getPropertyValue(element, 
					attributeNames[i], beanFactory);
			
			if (pv != null) {
				pvs.addPropertyValue(pv);
			}
		}
		return pvs;
	}
	
	public static PropertyValue getPropertyValue(Element element, String attr,
			BeanFactory beanFactory) {

		String property = null;
		int i = attr.indexOf('=');
		if (i != -1) {
			property = attr.substring(0, i);
			attr = attr.substring(i + 1);
		}

		boolean beanRef = attr.charAt(0) == '@';
		if (beanRef) {
			Assert.notNull(beanFactory, "A BeanFactory must be passed in " +
					"order to resolve references");
			
			attr = attr.substring(1);
		}

		if (property == null) {
			property = FormatUtils.xmlToCamelCase(attr);
		}

		String value = getAttribute(element, attr);
		if (value != null) {
			if (beanRef) {
				return new PropertyValue(property, beanFactory.getBean(value));
			}
			return new PropertyValue(property, value);
		}
		return null;
	}

	public static void populate(Object bean, List<Element> elements,
			BeanFactory beanFactory) {

		BeanWrapper beanWrapper = new BeanWrapperImpl(bean);
		for (Element ele : elements) {
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

	public static Document createDocument() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			return dbf.newDocumentBuilder().newDocument();
		}
		catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	public static void removeNode(Node node) {
		if (node != null) {
			Node parent = node.getParentNode();
			if (parent != null) {
				parent.removeChild(node);
			}
		}
	}

	public static void importAndAppend(Node node, Node parent) {
		parent.appendChild(parent.getOwnerDocument().importNode(node, true));
	}

	public static void removeAllChildNodes(Node node) {
		while (node.hasChildNodes()) {
			node.removeChild(node.getLastChild());
		}
	}

	public static void setTextValue(Element element, String value) {
		removeAllChildNodes(element);
		if (value != null) {
			Node text = element.getOwnerDocument().createTextNode(value);
			element.appendChild(text);
		}
	}

	public static Document parse(String xml) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder parser = dbf.newDocumentBuilder();
			return parser.parse(new InputSource(new StringReader(xml)));
		}
		catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		catch (SAXException e) {
			throw new RuntimeException(e);
		}
	}

	public static String serialize(Node node) {
		try {
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			Source source = new DOMSource(node);
			StringWriter sw = new StringWriter();
			Result result = new StreamResult(sw);
			transformer.transform(source, result);
			return sw.toString();
		}
		catch (TransformerException e) {
			throw new RuntimeException(e);
		}
	}

}

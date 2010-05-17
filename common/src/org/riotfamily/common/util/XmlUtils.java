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
package org.riotfamily.common.util;

import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Utility class to work with DOM elements.
 */
public class XmlUtils {

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

}

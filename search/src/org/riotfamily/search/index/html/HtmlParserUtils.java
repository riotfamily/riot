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
package org.riotfamily.search.index.html;

import org.htmlparser.NodeFilter;
import org.htmlparser.Tag;
import org.htmlparser.beans.StringBean;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.tags.MetaTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class HtmlParserUtils {

	/**
	 * Extracts all text nodes from the given NodeList that are accepted by
	 * the specified filter. 
	 */
	public static String extractText(NodeList nodeList, NodeFilter filter) {
		return toText(nodeList.extractAllNodesThatMatch(filter, true));
	}
	
	/**
	 * Extracts all text nodes from the given NodeList. Non-breaking spaces
	 * are replaced by normal space characters. Subsequent whitespace characters
	 * are collapsed to a single character.
	 */
	public static String toText(NodeList nodeList) {
		try {
			StringBean toStringVisitor = new StringBean();
			nodeList.visitAllNodesWith(toStringVisitor);
			return toStringVisitor.getStrings();
		}
		catch (ParserException e) {
			return null;
		}
	}
	
	/**
	 * Returns the specified attribute value of the first node that is accepted 
	 * by the given filter.
	 */
	public static String extractAttribute(NodeList nodeList, NodeFilter filter,
		String attributeName) throws ParserException {

		NodeList matches = nodeList.extractAllNodesThatMatch(filter, true);
		if (matches.size() > 0) {
			return ((TagNode) matches.elementAt(0)).getAttribute(attributeName);
		}
		
		return null;
	}
	
	/**
	 * Returns the <code>content</code> attribute of the first <code>META</code>
	 * tag with the specified name.
	 */
	public static String getMeta(NodeList nodeList, String name) {
		return getMeta(nodeList, name, false);
	}
	
	/**
	 * Returns the content of the first <code>META</code> tag with the 
	 * specified <code>http-eqiv</code> attribute.
	 */
	public static String getHttpEquiv(NodeList nodeList, String name) {
		return getMeta(nodeList, name, true);
	}
	
	private static String getMeta(NodeList nodeList, String name,
			boolean httpEquiv) {
	
		NodeFilter filter = new AndFilter(
				new NodeClassFilter(MetaTag.class),
				new AttributeNodeFilter(httpEquiv ? "http-equiv" : "name", name));
		
		NodeList nodes = nodeList.extractAllNodesThatMatch(filter, true);
		if (nodes.size() > 0) {
			Tag tag = (Tag) nodes.elementAt(0);
			return tag.getAttribute("content");
		}
		return null;
	}
	
}

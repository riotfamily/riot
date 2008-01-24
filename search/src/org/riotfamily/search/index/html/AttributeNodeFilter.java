package org.riotfamily.search.index.html;

import org.htmlparser.Attribute;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Tag;

/**
 * NodeFilter that accepts all tags that have a certain attribute (and 
 * optionally a certain attribute value). In addition to the HasAttributeFilter
 * class provided by htmlparser, the case of the attribute value is ignored.
 */
public class AttributeNodeFilter implements NodeFilter {

	private String name;

	private String value;

	/**
	 * Creates a new AttributeFilter that accepts tags with the given attribute.
	 * 
	 * @param attribute The attribute to search for.
	 */
	public AttributeNodeFilter(String attribute) {
		this(attribute, null);
	}

	/**
	 * Creates a new AttributeFilter that accepts tags with the
	 * given attribute and value.
	 * 
	 * @param attribute The attribute to search for.
	 * @param value The value that must be matched, 
	 * 		or null if any value will match.
	 */
	public AttributeNodeFilter(String attribute, String value) {
		this.name = attribute;
		this.value = value;
	}

	/**
	 * Accept tags with a certain attribute.
	 * 
	 * @param node The node to check.
	 * @return <code>true</code> if the node has the attribute (and value if
	 *         that is being checked too), <code>false</code> otherwise.
	 */
	public boolean accept(Node node) {
		if (node instanceof Tag) {
			Tag tag = (Tag) node;
			Attribute attribute = tag.getAttributeEx(name);
			if (attribute != null) {
				if (this.value == null) {
					return true;
				}
				return this.value.equalsIgnoreCase(attribute.getValue());
			}
		}
		return false;
	}
}
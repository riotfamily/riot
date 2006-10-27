/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.search.parser.support;

import org.htmlparser.Attribute;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Tag;

/**
 * This class accepts all tags that have a certain attribute, and optionally,
 * with a certain value. In addition to the HasAttributeFilter class provided
 * by htmlparser, the case of the attribute value is ignored.
 */
public class AttributeFilter implements NodeFilter {

	private String name;

	private String value;


	/**
	 * Creates a new AttributeFilter that accepts tags with the given attribute.
	 * 
	 * @param attribute The attribute to search for.
	 */
	public AttributeFilter(String attribute) {
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
	public AttributeFilter(String attribute, String value) {
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

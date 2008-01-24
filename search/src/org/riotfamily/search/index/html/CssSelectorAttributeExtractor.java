package org.riotfamily.search.index.html;

import org.htmlparser.NodeFilter;
import org.htmlparser.filters.CssSelectorNodeFilter;
import org.htmlparser.util.ParserException;
import org.riotfamily.crawler.PageData;

public class CssSelectorAttributeExtractor implements FieldExtractor {
	
	private NodeFilter nodeFilter;
	private String attributeName;
	
	/**
	 * Sets a NodeFilter that is used to extract the field value.
	 */
	public void setNodeFilter(NodeFilter nodeFilter) {
		this.nodeFilter = nodeFilter;
	}
	
	/**
	 * Sets a CSS selector that is used to extract the field value.
	 */
	public void setCssSelector(String cssSelector) {
		nodeFilter = new CssSelectorNodeFilter(cssSelector);
	}
	
	public String getFieldValue(PageData pageData) {
		try {
			return HtmlParserUtils.extractAttribute(
				pageData.getNodes(), nodeFilter, attributeName);			
		}
		catch (ParserException e) {			
		}
		return null;
	}

	/**
	 * Sets the name of the attribute to extract.
	 */
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
}

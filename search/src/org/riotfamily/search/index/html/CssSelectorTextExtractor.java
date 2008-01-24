package org.riotfamily.search.index.html;

import org.htmlparser.NodeFilter;
import org.htmlparser.filters.CssSelectorNodeFilter;
import org.htmlparser.util.NodeList;
import org.riotfamily.crawler.PageData;

/**
 * {@link FieldExtractor} that extracts the text from all nodes matched by a
 * CSS selector.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class CssSelectorTextExtractor implements FieldExtractor {
	
	private NodeFilter nodeFilter;
	
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
	
	/**
	 * Extracts the text from all nodes matched by the configured CSS selector.
	 * @see CssSelectorNodeFilter
	 * @see HtmlParserUtils#extractText(NodeList, NodeFilter)
	 */
	public String getFieldValue(PageData pageData) {
		return HtmlParserUtils.extractText(pageData.getNodes(), nodeFilter);			
	}
	
}

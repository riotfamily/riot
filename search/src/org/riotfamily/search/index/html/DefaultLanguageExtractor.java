package org.riotfamily.search.index.html;

import org.htmlparser.NodeFilter;
import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;
import org.riotfamily.crawler.PageData;

/**
 * {@link FieldExtractor} that tries to determine the document language.
 * It first looks for a <code>Content-Language</code> header or http-equiv 
 * meta-tag. As fallback the first <code>lang</code> attribute (no matter on 
 * which tag) is returned.
 *    
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class DefaultLanguageExtractor implements FieldExtractor {

	private static final String LANG_ATTRIBUTE = "lang";
	
	private static final String LANG_HEADER = "content-language";

	public String getFieldValue(PageData pageData) {
		String lang = pageData.getHeader(LANG_HEADER);
		if (lang == null) {
			lang = HtmlParserUtils.getHttpEquiv(pageData.getNodes(), LANG_HEADER);
			if (lang == null) {
				NodeFilter filter = new AttributeNodeFilter(LANG_ATTRIBUTE);
				NodeList nodes = pageData.getNodes().extractAllNodesThatMatch(filter, true);
				if (nodes.size() > 0) {
					Tag tag = (Tag) nodes.elementAt(0);
					lang = tag.getAttribute(LANG_ATTRIBUTE);
				}
			}
		}
		return lang;
	}

}

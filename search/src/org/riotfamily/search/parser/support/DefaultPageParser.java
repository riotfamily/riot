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

import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.beans.StringBean;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.CssSelectorNodeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.BodyTag;
import org.htmlparser.tags.HeadingTag;
import org.htmlparser.tags.MetaTag;
import org.htmlparser.tags.TitleTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;
import org.riotfamily.search.crawler.PageData;
import org.riotfamily.search.parser.Page;
import org.riotfamily.search.parser.PageParser;

public class DefaultPageParser implements PageParser {
	
	private NodeFilter titleFilter = new NodeClassFilter(TitleTag.class);
	
	private NodeFilter contentFilter = new NodeClassFilter(BodyTag.class);
	
	private NodeFilter headingsFilter = new NodeClassFilter(HeadingTag.class);

	private NodeFilter linkFilter = new AttributeFilter("href");
	
	
	public DefaultPageParser() {
	}
	
	public DefaultPageParser(String contentCssSelector) {
		if (contentCssSelector != null) {
			setContentFilter(new CssSelectorNodeFilter(contentCssSelector));
		}
	}

	public void setContentFilter(NodeFilter contentFilter) {
		this.contentFilter = contentFilter;
	}

	public void setHeadingsFilter(NodeFilter headingsFilter) {
		this.headingsFilter = headingsFilter;
	}

	public void setLinkFilter(NodeFilter linkFilter) {
		this.linkFilter = linkFilter;
	}

	public void setTitleFilter(NodeFilter titleFilter) {
		this.titleFilter = titleFilter;
	}

	public Page parsePage(PageData pageData) throws Exception {
		Page page = new Page();
		page.setUrl(pageData.getUrl());
		Parser parser = new Parser();
		parser.setInputHTML(pageData.getHtml());
		NodeList doc = parser.parse(null);
		NodeList nodesToIndex = doc.extractAllNodesThatMatch(
				contentFilter, true);

		page.setTitle(extractText(doc, titleFilter));
		
		page.setLanguage(getHttpEquiv(doc, "content-language"));
		page.setKeywords(getMeta(doc, "keywords"));
		page.setDescription(getMeta(doc, "description"));
		page.setRobots(getMeta(doc, "robots"));
		
		page.setHeadings(extractText(nodesToIndex, headingsFilter));
		page.setContent(toText(nodesToIndex));	
		
		NodeList links = doc.extractAllNodesThatMatch(linkFilter, true);
		SimpleNodeIterator it = links.elements();
		while (it.hasMoreNodes()) {
			Tag tag = (Tag) it.nextNode();
			page.addLink(tag.getAttribute("href"));	
		}
	
		return page;
	}
	
	protected String extractText(NodeList nodeList, NodeFilter filter) 
			throws ParserException {
		
		return toText(nodeList.extractAllNodesThatMatch(filter, true));
	}
	
	protected String getMeta(NodeList nodeList, String name) 
			throws ParserException {
		
		return getMeta(nodeList, name, false);
	}
	
	protected String getHttpEquiv(NodeList nodeList, String name) 
			throws ParserException {
	
		return getMeta(nodeList, name, true);
	}
	
	protected String getMeta(NodeList nodeList, String name, 
			boolean httpEquiv) throws ParserException {
	
		NodeFilter filter = new AndFilter(
				new NodeClassFilter(MetaTag.class),
				new AttributeFilter(httpEquiv? "http-equiv" : "name", name));
		
		NodeList nodes = nodeList.extractAllNodesThatMatch(filter, true);
		if (nodes.size() > 0) {
			Tag tag = (Tag) nodes.elementAt(0);
			return tag.getAttribute("content");
		}
		return null;
	}
		
	
	protected String toText(NodeList nodeList) throws ParserException {
		StringBean toStringVisitor = new StringBean();
		nodeList.visitAllNodesWith(toStringVisitor);
		return toStringVisitor.getStrings();
	}

}

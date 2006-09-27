package org.riotfamily.search.parser;

import java.util.Collection;

import org.riotfamily.search.crawler.LinkExtractor;
import org.riotfamily.search.crawler.PageData;

public class HtmlLinkExtractor implements LinkExtractor {

	private PageParser pageParser;
	
	private PageHandler pageProcessor;
	
	public HtmlLinkExtractor(PageParser parser, PageHandler processor) {
		this.pageParser = parser;
		this.pageProcessor = processor;
	}

	public final Collection extractLinks(PageData pageData) {
		try {
			Page page = pageParser.parsePage(pageData);
			if (pageProcessor != null) {
				pageProcessor.handlePage(page);
			}
			return page.getLinks();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}

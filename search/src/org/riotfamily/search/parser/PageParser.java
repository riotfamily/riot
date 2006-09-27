package org.riotfamily.search.parser;

import org.riotfamily.search.crawler.PageData;

public interface PageParser {

	public Page parsePage(PageData pageData) throws Exception;

}
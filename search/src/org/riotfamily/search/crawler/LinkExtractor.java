package org.riotfamily.search.crawler;

import java.util.Collection;

public interface LinkExtractor {

	public Collection extractLinks(PageData pageData);
}

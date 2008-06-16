package org.riotfamily.crawler;

import java.util.List;

/**
 * Interface to extract all links from a parsed HTML document.
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public interface LinkExtractor {

	public List<String> extractLinks(PageData pageData);

}

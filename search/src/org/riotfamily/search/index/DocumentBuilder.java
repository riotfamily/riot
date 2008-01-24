package org.riotfamily.search.index;

import org.apache.lucene.document.Document;
import org.riotfamily.crawler.PageData;

public interface DocumentBuilder {

	public static final String URL = "url";
	
	public static final String CONTENT_TYPE = "contentType";
	
	public static final String LANGUAGE = "language";
	
	public static final String TITLE = "title";
	
	public static final String CONTENT = "content";
	
	public static final String KEYWORDS = "keywords";
	
	public static final String[] SEARCH_FIELDS = new String[] {
		URL, TITLE, CONTENT, KEYWORDS
	};
	
	public Document buildDocument(PageData pageData);

}

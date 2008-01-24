package org.riotfamily.search.index.html;

import org.riotfamily.crawler.PageData;
import org.riotfamily.search.index.Indexer;

/**
 * Interface to add custom fields to the full-text index.
 * 
 * @see Indexer#addCustomFieldExtractor(String, FieldExtractor)
 * @see Indexer#setCustomFieldExtractors(java.util.Map) 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public interface FieldExtractor {
	
	public String getFieldValue(PageData pageData);

}

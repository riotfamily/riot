package org.riotfamily.search.index.html;

import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.TitleTag;

/**
 * {@link FieldExtractor} that extracts the content of the document's 
 * <code>TITLE</code> tag.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class TitleTagExtractor extends CssSelectorTextExtractor {
	
	public TitleTagExtractor() {
		setNodeFilter(new NodeClassFilter(TitleTag.class));
	}

}

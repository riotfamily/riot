package org.riotfamily.search.index;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.riotfamily.search.parser.Page;
import org.riotfamily.search.parser.PageHandler;

/**
 * PageProcessor that creates a Lucene document and adds it to the index. 
 */
public class IndexingPageHandler implements PageHandler {

	private IndexWriter indexWriter;
	
	private PagePreparator pagePreparator;
	
	public IndexingPageHandler(IndexWriter writer) {
		this.indexWriter = writer;
	}
	
	public IndexingPageHandler(IndexWriter writer, PagePreparator preparator) {
		this.indexWriter = writer;
		this.pagePreparator = preparator;
	}

	public void setPagePreparator(PagePreparator pagePreparator) {
		this.pagePreparator = pagePreparator;
	}

	public void handlePage(Page page) throws Exception {
		if (page.isIndex()) {
			if (pagePreparator != null) {
				pagePreparator.preparePage(page);
			}
			indexWriter.addDocument(page.toLuceneDocument(), getAnalyzer(page));
		}
	}
	
	protected Analyzer getAnalyzer(Page page) {
		return indexWriter.getAnalyzer();
	}


}

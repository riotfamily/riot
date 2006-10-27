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

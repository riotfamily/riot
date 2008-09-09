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
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.search.index;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;

import org.riotfamily.common.log.RiotLog;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.riotfamily.crawler.PageData;
import org.riotfamily.crawler.PageHandler;
import org.riotfamily.search.analysis.AnalyzerFactory;
import org.riotfamily.search.analysis.DefaultAnalyzerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.util.WebUtils;

/**
 * PageHandler that creates Lucene documents and adds them to the search  index.
 */
public class Indexer implements PageHandler,
		ServletContextAware, InitializingBean {

	private RiotLog log = RiotLog.get(Indexer.class);

	private Directory indexDir;

	private File tempDir;

	private boolean compound;

	private DocumentBuilder documentBuilder;
	
	private AnalyzerFactory analyzerFactory;
	
	private Directory tempIndexDir;

	private IndexWriter tempWriter;
	
	/**
	 * Sets the location where the index should be stored. The given resource
	 * must point into the file system, i.e. resource.getFile() must return a
	 * File object.
	 */
	public void setIndexLocation(Resource resource) throws IOException {
		setIndexDir(resource.getFile());
	}

	/**
	 * Sets the location where the index should be stored. The given file must
	 * point to a writable directory. If the directory does not exist it will
	 * be created.
	 */
	public void setIndexDir(File dir) throws IOException {
		dir.mkdirs();
		indexDir = FSDirectory.getDirectory(dir);
	}

	public void setDocumentBuilder(DocumentBuilder documentBuilder) {
		this.documentBuilder = documentBuilder;
	}
	
	/**
	 * Sets the AnalyzerFactory to be used.
	 */
	public void setAnalyzerFactory(AnalyzerFactory analyzerFactory) {
		this.analyzerFactory = analyzerFactory;
	}

	/**
	 * Sets whether a compound index file should be used.
	 * @see IndexWriter#setUseCompoundFile(boolean)
	 */
	public void setCompound(boolean compound) {
		this.compound = compound;
	}

	public void setServletContext(ServletContext servletContext) {
		tempDir = WebUtils.getTempDir(servletContext);
	}

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(documentBuilder, "A DocumentBuilder must be set.");
		if (indexDir == null) {
			setIndexDir(new File(tempDir, "search-index"));
		}
		if (analyzerFactory == null) {
			analyzerFactory = new DefaultAnalyzerFactory();
		}
		File d = new File(tempDir, "temp-search-index");
		d.mkdir();
		tempIndexDir = FSDirectory.getDirectory(d);
	}

	private Analyzer getAnalyzer(Document document) {
		String language = document.get(DocumentBuilder.LANGUAGE);
		return analyzerFactory.getAnalyzer(language);
	}
	
	/**
	 * Creates a new IndexWriter that writes to a temporary location. When
	 * {@link #crawlerFinished()} is invoked, this temporary index is moved to
	 * its final destination.
	 */
	public void crawlerStarted() {
		try {
			tempWriter = new IndexWriter(tempIndexDir, null, true);
			tempWriter.setUseCompoundFile(compound);
		}
		catch (IOException e) {
			log.error(e);
		}
	}

	public void handlePage(PageData pageData) {
		if (tempWriter == null) {
			return;
		}
		try {
			Document document = documentBuilder.buildDocument(pageData); 
			if (document != null) {
				tempWriter.addDocument(document, getAnalyzer(document));
			}
		}
		catch (IOException e) {
			log.error("Error indexing page", e);
		}
	}

	public void handlePageIncremental(PageData pageData) {
		try {
			log.info("Updating index for " + pageData.getUrl());
			boolean indexExists = IndexReader.indexExists(indexDir);
			if (indexExists) {
				IndexReader reader = IndexReader.open(indexDir);
				reader.deleteDocuments(new Term(DocumentBuilder.URL, pageData.getUrl()));
				reader.close();
			}
			Document doc = documentBuilder.buildDocument(pageData);
			if (doc != null) {
				IndexWriter indexWriter = new IndexWriter(indexDir, 
						getAnalyzer(doc), !indexExists);
				
				indexWriter.addDocument(doc);
				indexWriter.close();
			}
		}
		catch (IOException e) {
			log.error("Error indexing page", e);
		}
	}

	public void crawlerFinished() {
		try {
			tempWriter.close();
			IndexWriter indexWriter = new IndexWriter(indexDir, null, true);
			indexWriter.addIndexes(new Directory[] { tempIndexDir });
			indexWriter.close();
		}
		catch (IOException e) {
			log.error(e);
		}
		tempWriter = null;
	}

}

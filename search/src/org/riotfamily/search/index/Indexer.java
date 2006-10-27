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


import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.riotfamily.search.crawler.Crawler;
import org.riotfamily.search.crawler.CrawlerModel;
import org.riotfamily.search.crawler.LinkExtractor;
import org.riotfamily.search.crawler.LinkFilter;
import org.riotfamily.search.crawler.PageLoader;
import org.riotfamily.search.crawler.support.CommonsHttpClientPageLoader;
import org.riotfamily.search.crawler.support.LocalLinkFilter;
import org.riotfamily.search.index.support.DefaultPagePreparator;
import org.riotfamily.search.parser.HtmlLinkExtractor;
import org.riotfamily.search.parser.PageParser;
import org.riotfamily.search.parser.support.DefaultPageParser;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.util.WebUtils;

/**
 * Crawls a website and adds pages to a Lucene index.
 */
public class Indexer implements ServletContextAware, InitializingBean {

	private Directory indexDir;
	
	private File tempDir;
	
	private String startPage = "http://localhost:8080";
		
	private String pathPattern = null;
	
	private boolean compound;
	
	private PageLoader pageLoader;
	
	private PageParser pageParser;
	
	private PagePreparator pagePreparator;
	
	private LinkFilter linkFilter;
	
	private CrawlerModel crawlerModel;
	
	
	private Analyzer analyzer = new StandardAnalyzer();
	
	public void setIndexDir(Resource resource) throws IOException {
		File f = resource.getFile();
		f.mkdirs();
		indexDir = FSDirectory.getDirectory(f, true);
	}
	
	public void setAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	public void setCompound(boolean compound) {
		this.compound = compound;
	}

	public void setStartPage(String startPage) {
		this.startPage = startPage;
	}
	
	public void setPathPattern(String pathPattern) {
		this.pathPattern = pathPattern;
	}

	public void setPageLoader(PageLoader pageLoader) {
		this.pageLoader = pageLoader;
	}

	public void setPageParser(PageParser pageParser) {
		this.pageParser = pageParser;
	}

	public void setPagePreparator(PagePreparator pagePreparator) {
		this.pagePreparator = pagePreparator;
	}

	public void setServletContext(ServletContext servletContext) {
		tempDir = WebUtils.getTempDir(servletContext);		
	}	
	
	public void setLinkFilter(LinkFilter linkFilter) {
		this.linkFilter = linkFilter;
	}	

	public void setCrawlerModel(CrawlerModel crawlerModel) {
		this.crawlerModel = crawlerModel;
	}

	public void afterPropertiesSet() throws Exception {
		if (pageParser == null) {
			pageParser = new DefaultPageParser();
		}
		if (pageLoader == null) {
			pageLoader = new CommonsHttpClientPageLoader();
		}
		if (pagePreparator == null) {
			pagePreparator = new DefaultPagePreparator();
		}
	}
	
	public void index() throws IOException {
		File d = new File(tempDir, "index");
		d.mkdir();
		Directory tempIndexDir = FSDirectory.getDirectory(d, true);
		IndexWriter tempWriter = new IndexWriter(tempIndexDir, analyzer, true);
		tempWriter.setUseCompoundFile(compound);

		IndexingPageHandler handler = new IndexingPageHandler(
				tempWriter, pagePreparator);
		
		LinkExtractor linkExtractor = new HtmlLinkExtractor(pageParser, handler);
		
		if (linkFilter == null) {
			linkFilter = new LocalLinkFilter(pathPattern);
		}		
		
		Crawler crawler = new Crawler();
		crawler.setInitialUrl(startPage);
		crawler.setPageLoader(pageLoader);
		crawler.setLinkExtractor(linkExtractor);
		crawler.setLinkFilter(linkFilter);
		if (crawlerModel != null) {
			crawler.setModel(crawlerModel);
		}
		crawler.crawl();
		
		tempWriter.close();
		
		IndexWriter indexWriter = new IndexWriter(indexDir, analyzer, true);
		indexWriter.addIndexes(new Directory[] { tempIndexDir });
		indexWriter.close();
	}

}

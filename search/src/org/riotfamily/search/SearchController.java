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
package org.riotfamily.search;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.CachingWrapperFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.search.ResultHighlighter.HighlightingContext;
import org.riotfamily.search.analysis.AnalyzerFactory;
import org.riotfamily.search.analysis.DefaultAnalyzerFactory;
import org.riotfamily.search.index.DocumentBuilder;
import org.riotfamily.website.generic.view.Pager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.WebUtils;

public class SearchController implements Controller, InitializingBean {

	private static final String RESULT_MODEL_KEY = "result";

	private static final String PAGER_MODEL_KEY = "pager";

	private Directory indexDir;

	private AnalyzerFactory analyzerFactory;

	private ResultHighlighter resultHighlighter = new ResultHighlighter();
	
	private String queryParam = "search";

	private String pageParam = "page";

	private String pageSizeParam = "pageSize";

	private int defaultPageSize = 10;

	private int pagerPadding = 5;

	private String viewName = ResourceUtils.getPath(
			SearchController.class, "SearchView.ftl");

	public void setDefaultPageSize(int defaultPageSize) {
		this.defaultPageSize = defaultPageSize;
	}

	public void setPageParam(String pageParam) {
		this.pageParam = pageParam;
	}

	public void setPageSizeParam(String pageSizeParam) {
		this.pageSizeParam = pageSizeParam;
	}

	public void setQueryParam(String queryParam) {
		this.queryParam = queryParam;
	}

	public void setPagerPadding(int pagerPadding) {
		this.pagerPadding = pagerPadding;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	
	public void setIndexDir(Resource resource) throws IOException {
		File f = resource.getFile();
		f.mkdirs();
		indexDir = FSDirectory.getDirectory(f);
	}

	public void setAnalyzerFactory(AnalyzerFactory analyzerFactory) {
		this.analyzerFactory = analyzerFactory;
	}
	
	public void afterPropertiesSet() throws Exception {
		if (analyzerFactory == null) {
			analyzerFactory = new DefaultAnalyzerFactory();
		}
	}
	
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String queryString = request.getParameter(queryParam);
		
		int page = ServletRequestUtils.getIntParameter(request, pageParam, 1);
		int pageSize = ServletRequestUtils.getIntParameter(
				request, pageSizeParam, defaultPageSize);

		int offset = (page - 1) * pageSize;

		SearchResult result = new SearchResult();
		result.setOriginalQuery(queryString);
		result.setPage(page);
		result.setPageSize(pageSize);

		if (!IndexReader.indexExists(indexDir)) {
			return onEmptyIndex(result, request);
		}
			
		Query query = createQuery(queryString, request);
		if (query != null) {
			IndexSearcher indexSearcher = new IndexSearcher(indexDir);
			Filter filter = createFilter(request);
			Hits hits = indexSearcher.search(query, filter);
			if (hits.length() == 0) {
				return onEmptyResult(result, request);
			}
			HighlightingContext highlightingContext =
					resultHighlighter.createContext(indexSearcher, query);

			result.setHits(hits, offset, pageSize, highlightingContext);
			indexSearcher.close();
			
			return onResultView(result, request);
		}
		else {
			return onEmptyQuery(result, request);
		}
	}

	protected ModelAndView onResultView(SearchResult result, HttpServletRequest request) {
		Pager pager = new Pager(result.getPage(), result.getPageSize(),
			result.getTotalHitCount());
		pager.initialize(request, pagerPadding, pageParam);

		return new ModelAndView(viewName)
			.addObject(RESULT_MODEL_KEY, result)
			.addObject(PAGER_MODEL_KEY, pager);
	}
	
	protected ModelAndView onEmptyIndex(SearchResult result, HttpServletRequest request) {
		return onEmptyResult(result, request);
	}
	
	protected ModelAndView onEmptyQuery(SearchResult result, HttpServletRequest request) {
		return onEmptyResult(result, request);
	}
	
	protected ModelAndView onEmptyResult(SearchResult result, HttpServletRequest request) {
		return new ModelAndView(viewName).addObject(RESULT_MODEL_KEY, result);
	}
	
	protected Analyzer getAnalyzer(HttpServletRequest request) {
		return analyzerFactory.getAnalyzer(getLanguage(request));
	}
	
	protected String getLanguage(HttpServletRequest request) {
		return RequestContextUtils.getLocale(request).getLanguage();
	}
	
	protected Query createQuery(String queryString, HttpServletRequest request) {
		if (StringUtils.hasText(queryString)) {
			return new SimpleSearchQueryParser(getAnalyzer(request)).parse(
					queryString, DocumentBuilder.SEARCH_FIELDS);			
		}
		return null;
	}
	
	protected Filter createFilter(HttpServletRequest request) {
		Query filterQuery = createFilterQuery(request);
		
		if (filterQuery == null) {
			return null;
		}
		return new CachingWrapperFilter(new QueryWrapperFilter(filterQuery));
	}

	protected Query createFilterQuery(HttpServletRequest request) {
		Map<String, String> filterParams = WebUtils.getParametersStartingWith(request, "filter_");
		if (filterParams.isEmpty()) {
			return null;
		}
		BooleanQuery query = new BooleanQuery();
		Iterator<Map.Entry<String, String>> it = filterParams.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> entry = it.next();
			String field = entry.getKey();
			String value = entry.getValue();
			query.add(new TermQuery(new Term(field, value)), Occur.MUST);
		}
		return query;
	}

}

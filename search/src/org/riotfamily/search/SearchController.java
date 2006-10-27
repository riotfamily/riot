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
package org.riotfamily.search;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.QueryFilter;
import org.apache.lucene.search.TermQuery;
import org.riotfamily.common.collection.FlatMap;
import org.riotfamily.common.web.view.Pager;
import org.riotfamily.search.parser.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.support.RequestContextUtils;

public class SearchController implements Controller {

	private static final String RESULT_MODEL_KEY = "result";
	
	private static final String PAGER_MODEL_KEY = "pager";
	
	private String queryParam = "search";
	
	private String languageParam = "lang";
	
	private String pageParam = "page";
	
	private String pageSizeParam = "pageSize";
	
	private int defaultPageSize = 10;
	
	private int pagerPadding = 5;
	
	private String viewName;
	
	private Searcher searcher;
	
	private boolean useLangFromRequestLocale;
	
	public SearchController(Searcher searcher) {
		this.searcher = searcher;
	}

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

	public void setUseLangFromRequestLocale(boolean useLangFromRequestLocale) {
		this.useLangFromRequestLocale = useLangFromRequestLocale;
	}

	public ModelAndView handleRequest(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		String query = request.getParameter(queryParam);
		
		
		int page = ServletRequestUtils.getIntParameter(request, pageParam, 1);
		int pageSize = ServletRequestUtils.getIntParameter(
				request, pageSizeParam, defaultPageSize);
		
		int offset = (page - 1) * pageSize;
		
		SearchResult result = searcher.search(query, createFilter(request), offset, pageSize);
		Pager pager = new Pager(page, pageSize, result.getTotalHitCount());
		pager.initialize(request, pagerPadding, pageParam);
		
		FlatMap model = new FlatMap();
		model.put(RESULT_MODEL_KEY, result);
		model.put(PAGER_MODEL_KEY, pager);
		
		return new ModelAndView(viewName, model);
	}
	
	protected Filter createFilter(HttpServletRequest request) {
		String language = request.getParameter(languageParam);
		
		if (!StringUtils.hasLength(language) && useLangFromRequestLocale) {
			language = RequestContextUtils.getLocale(request).getLanguage();
		}
		if (language != null) {
			return new QueryFilter(
				new TermQuery(new Term(Page.LANGUAGE, language)));
		}
		return null;
	}

}

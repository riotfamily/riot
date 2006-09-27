package org.riotfamily.search;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.riotfamily.search.ResultHighlighter.HighlightingContext;
import org.riotfamily.search.parser.Page;
import org.riotfamily.search.support.OperatorMultiFieldQueryParser;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

public class Searcher {

	private Directory indexDir;
	
	private ResultHighlighter resultHighlighter = new ResultHighlighter();
	
	public void setIndexDir(Resource resource) throws IOException {
		File f = resource.getFile();
		f.mkdirs();
		indexDir = FSDirectory.getDirectory(f, false);
	}
	
	public SearchResult search(String queryString, Filter filter,
			int offset, int maxResults) throws Exception {
		
		SearchResult result = new SearchResult();
		result.setOriginalQuery(queryString);
		
		if (StringUtils.hasLength(queryString) && indexExists()) {
			IndexSearcher indexSearcher = new IndexSearcher(indexDir);
			Query query = createQuery(queryString);			
			Hits hits = indexSearcher.search(query, filter);
			HighlightingContext highlightingContext = 
					resultHighlighter.createContext(indexSearcher, query);
		
			result.setHits(hits, offset, maxResults, highlightingContext);
			indexSearcher.close();
		}
		return result;
	}

	protected Query createQuery(String queryString) throws ParseException {
		OperatorMultiFieldQueryParser queryParser =
				new OperatorMultiFieldQueryParser(new StandardAnalyzer());
		
		queryParser.setDefaultOperator(QueryParser.AND_OPERATOR);
		
		Query multiFieldQuery = queryParser.parse(queryString, Page.SEARCH_FIELDS);
		
		BooleanQuery query = new BooleanQuery();
		query.add(multiFieldQuery, BooleanClause.Occur.MUST);
		return query;
	}
	
	public boolean indexExists() throws IOException {
		return IndexReader.indexExists(indexDir);
	}
	
	
	
	
	
}

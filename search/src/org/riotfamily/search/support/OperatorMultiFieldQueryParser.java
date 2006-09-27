package org.riotfamily.search.support;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

public class OperatorMultiFieldQueryParser extends MultiFieldQueryParser {
	
	public OperatorMultiFieldQueryParser(Analyzer analyzer) {
		super(new String[0], analyzer);
	}
	
	public Query parse(String query, String[] fields)
			throws ParseException {
		
		BooleanQuery booleanQuery = new BooleanQuery();
		for (int i = 0; i < fields.length; i++) {
			QueryParser parser = new QueryParser(fields[i], getAnalyzer());
			parser.setDefaultOperator(getDefaultOperator());
			Query q = parser.parse(query);
			booleanQuery.add(q, BooleanClause.Occur.SHOULD);
		}
		return booleanQuery;
	}

}


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


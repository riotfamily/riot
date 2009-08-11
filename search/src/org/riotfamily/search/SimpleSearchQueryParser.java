/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.search;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.riotfamily.common.util.Generics;

public class SimpleSearchQueryParser {

	private Analyzer analyzer;

	public SimpleSearchQueryParser(Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	protected List<Token> getTokens(String text) {
		ArrayList<Token> tokens = Generics.newArrayList();
		try {
			TokenStream source = analyzer.tokenStream(null, new StringReader(text));
			Token token;
		    while ((token = source.next()) != null) {
		    	tokens.add(token);
		    }
		    source.close();
		}
		catch (IOException e) {
		}
		return tokens;
	}

	public Query parse(String text, String[] fields)  {
		List<Token> tokens = getTokens(text);
		BooleanQuery query = new BooleanQuery();
		for (int i = 0; i < fields.length; i++) {
			query.add(createFieldQuery(fields[i], tokens),
					BooleanClause.Occur.SHOULD);
		}
		return query;
	}

	protected Query createFieldQuery(String field, List<Token> tokens) {
		BooleanQuery query = new BooleanQuery();
		Iterator<Token> it = tokens.iterator();
		while (it.hasNext()) {
			Token token = it.next();
			query.add(createFieldTokenQuery(field, token),
					BooleanClause.Occur.SHOULD);
		}
		return query;
	}

	protected Query createFieldTokenQuery(String field, Token token) {
		return new PrefixQuery(new Term(field, token.termText()));
	}

}


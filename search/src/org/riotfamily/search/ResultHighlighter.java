package org.riotfamily.search;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;

public class ResultHighlighter {

	private int maxFragments = 3;
	
	private String separator = " ... ";
	
	private int fragmentSize = 75;
	
	private String fragmentPreTag = "<em>";
	
	private String fragmentPostTag = "</em>";
	
	private Fragmenter fragmenter = new SimpleFragmenter(fragmentSize);
	
	private Formatter formatter = new SimpleHTMLFormatter(
			fragmentPreTag, fragmentPostTag);

	
	public HighlightingContext createContext(
			IndexSearcher indexSearcher, Query query) 
			throws IOException {
		
		Scorer scorer = new QueryScorer(indexSearcher.rewrite(query));
		Highlighter highlighter = new Highlighter(formatter, scorer);
		highlighter.setTextFragmenter(fragmenter);
		return new HighlightingContext(highlighter);
	}
	
	public class HighlightingContext {
		
		private Highlighter highlighter;
		
		private HighlightingContext(Highlighter highlighter) {
			this.highlighter = highlighter;
		}

		public String getFragments(String content) {
			
			TokenStream ts = new SimpleAnalyzer().tokenStream(null, 
					new StringReader(content));
			
			String fragments = null;
			try {
				fragments = highlighter.getBestFragments(ts,
						content, maxFragments, separator);
			}
			catch (IOException e) {
			}
			if (fragments != null && fragments.length() > 0) {
				if (separator != null) {
					fragments = fragments + separator;
				}
				return fragments;
			}
			else {
				int excerptSize = maxFragments * fragmentSize;
				if (content.length() > excerptSize) {
					return content.substring(0, excerptSize)
							+ separator;
				}
				return content;
			}
		}
	}
}

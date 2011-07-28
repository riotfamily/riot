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

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Encoder;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLEncoder;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;

public class ResultHighlighter {

	private int fragmentSize = 75;
	
	private int maxFragments = 3;
	
	private String separator = " ... ";

	private String highlightPreTag = "em";

	private String highlightPostTag = "em";
	
	private Formatter formatter;
	
	private Encoder encoder = new SimpleHTMLEncoder();
	
	private Fragmenter fragmenter;
	
	/**
	 * Sets the number of characters per fragment.  Default is <code>75</code>.
	 */
	public void setFragmentSize(int fragmentSize) {
		this.fragmentSize = fragmentSize;
	}
	
	/**
	 * Sets the maximum number of fragments. Default is <code>3</code>.
	 */
	public void setMaxFragments(int maxFragments) {
		this.maxFragments = maxFragments;
	}
	
	/**
	 * Sets a string that is used to separate the fragments. 
	 * Default is <code>" ... "</code>.
	 */
	public void setSeparator(String separator) {
		this.separator = separator;
	}
	
	/**
	 * Sets a string that is used as html tag to higlight searched terms in the
	 * description snippet. Default is <code>"em"</code> which wraps searched
	 * terms within <code>"&lt;em>term&lt;/em>"</code>.
	 */
	public void setHighlightTag(String highlightTag) {
		this.highlightPreTag = highlightTag;
		this.highlightPostTag = highlightTag;
	}
	
	/**
	 * Sets a string that is used as html tag to before the searched term in the
	 * description snippet. Default is <code>"em"</code>. See {@link #setHighlightTag(String)}.
	 */
	public void setHighlightPreTag(String highlightPreTag) {
		this.highlightPreTag = highlightPreTag;
	}
	
	/**
	 * Sets a string that is used as html tag to after the searched term in the
	 * description snippet. Default is <code>"em"</code>. See {@link #setHighlightTag(String)}.
	 */
	public void setHighlightPostTag(String highlightPostTag) {
		this.highlightPostTag = highlightPostTag;
	}
	
	public HighlightingContext createContext(
			IndexSearcher indexSearcher, Query query) 
			throws IOException {
		
		Scorer scorer = new QueryScorer(indexSearcher.rewrite(query));
		if (formatter == null) {
			formatter = new SimpleHTMLFormatter("<" + highlightPreTag + ">", 
					"</" + highlightPostTag + ">");
		}
		if (fragmenter == null) {
			fragmenter = new SimpleFragmenter(fragmentSize);
		}
		Highlighter highlighter = new Highlighter(formatter, encoder, scorer);
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

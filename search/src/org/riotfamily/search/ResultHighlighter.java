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

	private String highlightTag = "em";
	
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

	public HighlightingContext createContext(
			IndexSearcher indexSearcher, Query query) 
			throws IOException {
		
		Scorer scorer = new QueryScorer(indexSearcher.rewrite(query));
		if (formatter == null) {
			formatter = new SimpleHTMLFormatter("<" + highlightTag + ">", 
					"</" + highlightTag + ">");
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

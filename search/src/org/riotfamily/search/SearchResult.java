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
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Hits;
import org.riotfamily.search.ResultHighlighter.HighlightingContext;
import org.riotfamily.search.index.DocumentBuilder;
import org.springframework.util.StringUtils;

public class SearchResult {
	
	private HighlightingContext highlightingContext;
	
	private List items;

	private int totalHitCount;
	
	private String originalQuery;
	
	private long searchDuration;
	
	private int page;
	
	private int pageSize;
	
	public void setHits(Hits hits, int offset, int maxResults, 
			HighlightingContext highlightingContext) 
			throws IOException {
		
		this.highlightingContext = highlightingContext;
		totalHitCount = hits.length();
		int end = Math.min(offset + maxResults, totalHitCount);
		items = new ArrayList(end - offset);
		for (int i = offset; i < end; i++) {
			Document doc = hits.doc(i);
			items.add(new Item(doc, hits.score(i)));
		}
	}

	public String getOriginalQuery() {
		return this.originalQuery;
	}

	public void setOriginalQuery(String originalQuery) {
		this.originalQuery = originalQuery;
	}

	public long getSearchDuration() {
		return this.searchDuration;
	}

	public void setSearchDuration(long searchDuration) {
		this.searchDuration = searchDuration;
	}

	public List getItems() {
		return this.items;
	}

	public int getTotalHitCount() {
		return this.totalHitCount;
	}
	
	public class Item {
		
		private Document doc;
		
		private String description;
		
		private float score;
		
		Item(Document doc, float score) {
			this.doc = doc;
			this.score = score;
			String content = doc.get(DocumentBuilder.CONTENT);
			if (StringUtils.hasText(content)) {
				this.description = highlightingContext.getFragments(content);
			}
		}

		public String getDescription() {
			return this.description;
		}

		public String getLink() {
			return doc.get(DocumentBuilder.URL);
		}

		public float getScore() {
			return this.score;
		}
	
		public Object get(String key) {
			return doc.get(key);
		}
	}

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
}

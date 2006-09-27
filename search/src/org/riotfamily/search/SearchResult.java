package org.riotfamily.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Hits;
import org.riotfamily.search.ResultHighlighter.HighlightingContext;
import org.riotfamily.search.parser.Page;
import org.springframework.util.StringUtils;

public class SearchResult {
	
	private HighlightingContext highlightingContext;
	
	private List items;

	private int totalHitCount;
	
	private String originalQuery;
	
	private String suggestedQuery;
	
	private long searchDuration;
	
	
	public void setHits(Hits hits, int offset, int maxResults, 
			HighlightingContext highlightingContext) 
			throws IOException {
		
		this.highlightingContext = highlightingContext;
		totalHitCount = hits.length();
		int end = Math.min(offset + maxResults, totalHitCount);
		items = new ArrayList(end - offset);
		for (int i = offset; i < end; i++) {
			Document doc = hits.doc(i);
			Page page = Page.fromLuceneDocument(doc);
			items.add(new Item(page, hits.score(i)));
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

	public String getSuggestedQuery() {
		return this.suggestedQuery;
	}

	public void setSuggestedQuery(String suggestedQuery) {
		this.suggestedQuery = suggestedQuery;
	}

	public List getItems() {
		return this.items;
	}

	public int getTotalHitCount() {
		return this.totalHitCount;
	}
	
	public class Item {
		
		private String title;
		
		private String description;
		
		private float score;
		
		private String link;
		
		Item(Page page, float score) {
			this.link = page.getUrl();
			this.title = page.getTitle();
			if (StringUtils.hasText(page.getContent())) {
				this.description = highlightingContext.getFragments(page.getContent());
			}
			this.score = score;
		}

		public String getDescription() {
			return this.description;
		}

		public String getLink() {
			return this.link;
		}

		public float getScore() {
			return this.score;
		}

		public String getTitle() {
			return this.title;
		}
	
	}
	
}

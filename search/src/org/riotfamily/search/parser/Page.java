package org.riotfamily.search.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

public class Page {
	
	public static final String URL = "url";
	
	public static final String TITLE = "title";
	
	public static final String LANGUAGE = "langauge";
	
	public static final String KEYWORDS = "keywords";
	
	public static final String DESCRIPTION = "description";
	
	public static final String HEADINGS = "headings";
	
	public static final String CONTENT = "content";
	
	public static final String[] SEARCH_FIELDS = {
		TITLE, KEYWORDS, DESCRIPTION, HEADINGS, CONTENT
	};
	
	private String url;
	
	private String title;
	
	private String language;
	
	private String keywords;
	
	private String description;
	
	private String robots;
	
	private String content;
	
	private String headings;

	private List links;
	
	
	public Page() {
	}
	
	public String getUrl() {
		return this.url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getHeadings() {
		return this.headings;
	}

	public void setHeadings(String headings) {
		this.headings = headings;
	}

	public String getKeywords() {
		return this.keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getLanguage() {
		return this.language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getRobots() {
		return this.robots;
	}

	public void setRobots(String robots) {
		this.robots = robots;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List getLinks() {
		return this.links;
	}

	public void setLinks(List links) {
		this.links = links;
	}
	
	public void addLink(String link) {
		if (links == null) {
			links = new ArrayList();
		}
		links.add(link);
	}
	
	public boolean isIndex() {
		return true;
	}

	public Document toLuceneDocument() {
		Document doc = new Document();
		doc.add(new Field(URL, url, 
				Field.Store.YES, Field.Index.UN_TOKENIZED));
		
		if (title != null) {
			doc.add(new Field(TITLE, title, 
					Field.Store.YES, Field.Index.TOKENIZED));
		}
		if (language != null) {
			doc.add(new Field(LANGUAGE, language, 
					Field.Store.YES, Field.Index.UN_TOKENIZED));
		}
		if (keywords != null) {
			doc.add(new Field(KEYWORDS, keywords, 
					Field.Store.NO, Field.Index.TOKENIZED));
		}
		if (description != null) {
			doc.add(new Field(DESCRIPTION, description, 
					Field.Store.YES, Field.Index.TOKENIZED));
		}
		if (headings != null) {
			doc.add(new Field(HEADINGS, headings, 
					Field.Store.NO, Field.Index.TOKENIZED));
		}
		if (content != null) {
			doc.add(new Field(CONTENT, content, 
					Field.Store.YES, Field.Index.TOKENIZED));
		}
		
		return doc;
	}
	
	public static Page fromLuceneDocument(Document doc) {
		Page page = new Page();
		page.setUrl(doc.get(URL));
		page.setTitle(doc.get(TITLE));
		page.setLanguage(doc.get(LANGUAGE));
		page.setKeywords(doc.get(KEYWORDS));
		page.setDescription(doc.get(DESCRIPTION));
		page.setHeadings(doc.get(HEADINGS));
		page.setContent(doc.get(CONTENT));
		return page;
	}
}

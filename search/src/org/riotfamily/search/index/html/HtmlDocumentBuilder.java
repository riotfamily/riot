package org.riotfamily.search.index.html;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.htmlparser.NodeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.BodyTag;
import org.htmlparser.tags.HeadingTag;
import org.htmlparser.util.NodeList;
import org.riotfamily.crawler.PageData;
import org.riotfamily.search.index.DocumentBuilder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

public class HtmlDocumentBuilder implements DocumentBuilder, InitializingBean {

	private NodeFilter contentFilter = new NodeClassFilter(BodyTag.class);

	private NodeFilter headingsFilter = new NodeClassFilter(HeadingTag.class);
	
	private FieldExtractor titleExtractor;
	
	private FieldExtractor languageExtractor;

	private Map customFieldExtractors;
	
	/**
	 * Sets a NodeFilter that is used to extract the ancestor(s) of the nodes
	 * that should be indexed.
	 */
	public void setContentFilter(NodeFilter contentFilter) {
		this.contentFilter = contentFilter;
	}

	/**
	 * Sets a NodeFilter that is used to extract headlines from the content.
	 */
	public void setHeadingsFilter(NodeFilter headingsFilter) {
		this.headingsFilter = headingsFilter;
	}

	/**
	 * Sets a FieldExtractor that is used to extract the page title.
	 */
	public void setTitleExtractor(FieldExtractor titleExtractor) {
		this.titleExtractor = titleExtractor;
	}
	
	/**
	 * Sets a FieldExtractor that is used to extract the document language.
	 * The returned value should be a lower case two-letter ISO code, or 
	 * <code>null</code> if the language can not be determined.
	 */
	public void setLanguageExtractor(FieldExtractor languageExtractor) {
		this.languageExtractor = languageExtractor;
	}

	/**
	 * Sets a {@link Map} of {@link FieldExtractor}s. To define an arbitrary
	 * field within your index, put a {@link FieldExtractor} in this
	 * {@link Map}. The key of this entry is used as the name of the field.
	 */
	public void setCustomFieldExtractors(Map customFieldExtractors) {
		this.customFieldExtractors = customFieldExtractors;
	}
	
	/**
	 * Adds a custom {@link FieldExtractor} for the specified field name.
	 */
	public void addCustomFieldExtractor(String field, FieldExtractor extractor) {
		if (customFieldExtractors == null) {
			customFieldExtractors = new TreeMap();
		}
		customFieldExtractors.put(field, extractor);
	}
	
	public void afterPropertiesSet() throws Exception {
		if (titleExtractor == null) {
			titleExtractor = new TitleTagExtractor();
		}
		if (languageExtractor == null) {
			languageExtractor = new DefaultLanguageExtractor();
		}
	}
	
	public Document buildDocument(PageData pageData) {
		NodeList nodes = pageData.getNodes();
		if (nodes == null) {
			return null;
		}
		String robots = HtmlParserUtils.getMeta(nodes, "robots");
		if (robots != null && robots.toLowerCase().indexOf("noindex") != -1) {
			return null;
		}
		
		Document doc = new Document();
		doc.add(new Field(URL, pageData.getUrl(), 
				Field.Store.YES, Field.Index.UN_TOKENIZED));

		doc.add(new Field(CONTENT_TYPE, "text/html", Field.Store.YES, 
				Field.Index.UN_TOKENIZED));
		
		String title = titleExtractor.getFieldValue(pageData);
		if (StringUtils.hasText(title)) {
			doc.add(new Field(TITLE, title, 
					Field.Store.YES, Field.Index.UN_TOKENIZED));
		}
		
		if (languageExtractor != null) {
			String language = languageExtractor.getFieldValue(pageData);
			if (StringUtils.hasText(language)) {
				doc.add(new Field(LANGUAGE, language, 
						Field.Store.NO, Field.Index.UN_TOKENIZED));
			}
		}
		
		addKeywords(doc, HtmlParserUtils.getMeta(nodes, "keywords"));
		addKeywords(doc, HtmlParserUtils.getMeta(nodes, "description"));
		
		NodeList nodesToIndex = nodes.extractAllNodesThatMatch(contentFilter, true);

		addKeywords(doc, HtmlParserUtils.extractText(nodesToIndex, headingsFilter));

		String content = HtmlParserUtils.toText(nodesToIndex);
		if (StringUtils.hasText(content)) {
			doc.add(new Field(CONTENT, content, 
					Field.Store.YES, Field.Index.TOKENIZED));
		}
		
		if (customFieldExtractors != null) {
			Iterator i = customFieldExtractors.entrySet().iterator();
			while (i.hasNext()) {
				Map.Entry entry = (Map.Entry) i.next();
				String key = (String) entry.getKey();
				FieldExtractor extractor = (FieldExtractor) entry.getValue(); 
				String value = extractor.getFieldValue(pageData);
				if (value != null) {
					doc.add(new Field(key, value, 
							Field.Store.YES, Field.Index.UN_TOKENIZED));
				}
			}
		}
		
		return doc;
	}
	
	private void addKeywords(Document doc, String keywords) {
		if (StringUtils.hasText(keywords)) {
			doc.add(new Field(KEYWORDS, keywords, 
					Field.Store.NO, Field.Index.TOKENIZED));
		}
	}

}

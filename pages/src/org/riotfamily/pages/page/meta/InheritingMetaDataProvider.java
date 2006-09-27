package org.riotfamily.pages.page.meta;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.pages.page.Page;

public abstract class InheritingMetaDataProvider implements MetaDataProvider {

	private String defaultTitle = "";
	
	private String defaultKeywords = "";
	
	private String defaultDescription = "";
	
	public void setDefaultDescription(String defaultDescription) {
		this.defaultDescription = defaultDescription;
	}

	public void setDefaultKeywords(String defaultKeywords) {
		this.defaultKeywords = defaultKeywords;
	}

	public void setDefaultTitle(String defaultTitle) {
		this.defaultTitle = defaultTitle;
	}

	public MetaData getMetaData(Page page, HttpServletRequest request) {
		MetaData meta = createMetaData(page, request);
		while (!meta.isComplete() && page.getParent() != null) {
			page = page.getParent();
			meta.fillIn(page.resolveMetaData(request));
		}
		if (!meta.isComplete()) {
			meta.fillIn(defaultTitle, defaultKeywords, defaultDescription);
		}
		return meta;
	}

	protected abstract MetaData createMetaData(Page page, 
			HttpServletRequest request);
	
}

package org.riotfamily.pages.page.meta;

public class MetaData {

	private String title;
	
	private String keywords;
	
	private String description;

	
	public MetaData() {
	}
	
	public MetaData(String title) {
		this.title = title;
	}

	public MetaData(String title, String keywords, String description) {
		this.title = title;
		this.keywords = keywords;
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getKeywords() {
		return this.keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public boolean isComplete() {
		return title != null && keywords != null && description != null;
	}
	
	public void fillIn(MetaData defaults) {
		fillIn(defaults.getTitle(), defaults.getKeywords(), 
				defaults.getDescription());
	}
	
	public void fillIn(String defaultTitle, String defaultKeywords, 
			String defaultDescription) {
		
		if (title == null) {
			title = defaultTitle;
		}
		if (keywords == null) {
			keywords = defaultKeywords;
		}
		if (description == null) {
			description = defaultDescription;
		}
	}
	
}

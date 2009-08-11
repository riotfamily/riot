package org.riotfamily.core.screen;


public class ScreenLink {

	private String title;
	
	private String url;
	
	private String icon;
	
	private boolean isNew;
	
	public ScreenLink(String title, String url, String icon, boolean isNew) {
		this.title = title;
		this.url = url;
		this.icon = icon;
		this.isNew = isNew;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public boolean isNew() {
		return isNew;
	}

}

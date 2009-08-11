package org.riotfamily.forms.resource;

import org.springframework.util.Assert;

/**
 *
 */
public class StylesheetResource implements FormResource {

	private String url;
	
	public StylesheetResource(String url) {
		Assert.notNull(url);
		this.url = url;
	}
	
	public String getUrl() {
		return this.url;
	}

	public void accept(ResourceVisitor visitor) {
		visitor.visitStyleSheet(this);
	}
	
	public int hashCode() {
		return url.hashCode();
	}
	
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj instanceof StylesheetResource) {
			StylesheetResource other = (StylesheetResource) obj;
			return this.url.equals(other.url);
		}
		return false;
	}

}
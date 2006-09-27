package org.riotfamily.forms.resource;

import java.io.PrintWriter;
import java.util.Collection;

import org.springframework.util.Assert;

/**
 *
 */
public class StylesheetResource implements FormResource {

	private String href;
	
	public StylesheetResource(String href) {
		Assert.notNull(href);
		this.href = href;
	}
	
	public void renderLoadingCode(PrintWriter writer, Collection loadedResources) {
		if (!loadedResources.contains(this)) {
			writer.print("Resources.loadStyleSheet('");
			writer.print(href);
			writer.print("');");
		}
	}
	
	public int hashCode() {
		return href.hashCode();
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
			return this.href.equals(other.href);
		}
		return false;
	}

}
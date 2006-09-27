package org.riotfamily.forms.resource;

import java.io.PrintWriter;
import java.util.Collection;

import org.springframework.util.Assert;

/**
 *
 */
public class ScriptResource implements FormResource {
	
	private String src;
	
	private String test;
	
	
	public ScriptResource(String src) {
		this(src, null);
	}
	
	public ScriptResource(String src, String test) {
		Assert.notNull(src);
		this.src = src;
		this.test = test;
	}
	
	public String getSrc() {
		return this.src;
	}

	public String getTest() {
		return this.test;
	}

	public void renderLoadingCode(PrintWriter writer, Collection loadedResources) {
		if (!loadedResources.contains(this)) {
			writer.print("Resources.loadScript('");
			writer.print(src);
			writer.print('\'');
			if (test != null) {
				writer.print(", '");
				writer.print(test);
				writer.print('\'');
			}
			writer.print(");");
		}
	}
	
	public int hashCode() {
		return src.hashCode();
	}
	
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj instanceof ScriptResource) {
			ScriptResource other = (ScriptResource) obj;
			return this.src.equals(other.src);
		}
		return false;
	}
}

package org.riotfamily.forms.resource;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.util.Assert;

/**
 *
 */
public class ScriptResource implements FormResource {
	
	private String url;
	
	private String test;
	
	private Collection<FormResource> dependencies;
	
	public ScriptResource(String url) {
		this(url, null);
	}
	
	public ScriptResource(String url, String test, FormResource... dependencies) {
		Assert.notNull(url);
		this.url = url;
		this.test = test;
		if (dependencies != null && dependencies.length > 0) {
			this.dependencies = Arrays.asList(dependencies);
		}
	}
	
	public String getUrl() {
		return this.url;
	}

	public String getTest() {
		return this.test;
	}

	public Collection<FormResource> getDependencies() {
		return this.dependencies;
	}

	public void accept(ResourceVisitor vistor) {
		vistor.visitScript(this);
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
		if (obj instanceof ScriptResource) {
			ScriptResource other = (ScriptResource) obj;
			return this.url.equals(other.url);
		}
		return false;
	}
}

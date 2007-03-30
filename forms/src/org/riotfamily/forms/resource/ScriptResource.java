/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
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
	
	private Collection dependencies;
	
	public ScriptResource(String url) {
		this(url, null);
	}
	
	public ScriptResource(String url, String test) {
		this(url, test, (FormResource[]) null);
	}
	
	public ScriptResource(String src, String test, FormResource dependency) {
		this(src, test, dependency != null 
				? new FormResource[] { dependency } 
				: null);
	}
	
	public ScriptResource(String url, String test, FormResource[] dependencies) {
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

	public Collection getDependencies() {
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

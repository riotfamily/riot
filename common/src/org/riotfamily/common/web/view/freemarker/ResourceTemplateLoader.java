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
package org.riotfamily.common.web.view.freemarker;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import freemarker.cache.TemplateLoader;

public class ResourceTemplateLoader implements TemplateLoader, 
		ResourceLoaderAware {

	private ResourceLoader resourceLoader;
	
	public ResourceTemplateLoader() {
	}
	
	public ResourceTemplateLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public Object findTemplateSource(String name) throws IOException {
		Resource resource = resourceLoader.getResource(name);
		return (resource.exists() ? resource : null);
	}

	public long getLastModified(Object templateSource) {
		Resource resource = (Resource) templateSource;
		try {
			return resource.lastModified();
		}
		catch (IOException e) {
			// If the resource cannot be resolved 
		}
		return -1;
	}

	public Reader getReader(Object templateSource, String encoding) throws IOException {
		Resource resource = (Resource) templateSource;
		return new InputStreamReader(resource.getInputStream(), encoding);
	}

	public void closeTemplateSource(Object templateSource) throws IOException {
	}

}

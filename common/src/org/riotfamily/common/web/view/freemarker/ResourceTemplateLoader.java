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
		
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public Object findTemplateSource(String name) throws IOException {
		Resource resource = resourceLoader.getResource(name);
		return (resource.exists() ? resource : null);
	}

	public long getLastModified(Object templateSource) {
		return -1;
	}

	public Reader getReader(Object templateSource, String encoding) throws IOException {
		Resource resource = (Resource) templateSource;
		return new InputStreamReader(resource.getInputStream(), encoding);
	}

	public void closeTemplateSource(Object templateSource) throws IOException {
	}

}

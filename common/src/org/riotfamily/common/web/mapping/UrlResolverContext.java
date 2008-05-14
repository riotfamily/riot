package org.riotfamily.common.web.mapping;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.web.servlet.PathCompleter;
import org.riotfamily.common.web.servlet.RequestPathCompleter;
import org.riotfamily.common.web.util.ServletUtils;
import org.springframework.util.Assert;

public class UrlResolverContext {

	private Map attributes;
	
	private PathCompleter pathCompleter;

	public UrlResolverContext(HttpServletRequest request) {
		this.attributes = ServletUtils.takeAttributesSnapshot(request);
		this.pathCompleter = new RequestPathCompleter(request);
	}
	
	public UrlResolverContext(Map attributes, PathCompleter pathCompleter) {
		this.attributes = attributes;
		this.pathCompleter = pathCompleter;
	}
	
	public Object getAttribute(String name) {
		if (attributes == null) {
			return null;
		}
		return attributes.get(name);
	}
	
	public String addServletMapping(String path) {
		Assert.notNull(pathCompleter, "The context has no PathCompleter");
		return pathCompleter.addServletMapping(path);
	}
	
	
}

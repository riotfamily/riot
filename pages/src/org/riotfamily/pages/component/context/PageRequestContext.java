package org.riotfamily.pages.component.context;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.web.util.ServletUtils;

public class PageRequestContext {

	private long timestamp;
	
	private long timeToLive;
	
	private Map parameters;
	
	private Map attributes;
	
	private String method;
	
	private String pathInfo;
	
	private String servletPath;
	
	private String queryString;
	
	private String requestURI;
	
	public PageRequestContext(HttpServletRequest request, long timeToLive) {
		this.timestamp = System.currentTimeMillis();
		this.timeToLive = timeToLive;
		
		this.method = request.getMethod();
		this.pathInfo = request.getPathInfo();
		this.servletPath = request.getServletPath();
		this.queryString = request.getQueryString();
		this.requestURI = request.getRequestURI();
		
		this.attributes = ServletUtils.takeAttributesSnapshot(request);
		this.parameters = new HashMap(request.getParameterMap());	
	}

	public boolean hasExpired() {
		return System.currentTimeMillis() - timestamp > timeToLive;
	}

	public void touch() {
		timestamp = System.currentTimeMillis();
	}

	public Map getAttributes() {
		return this.attributes;
	}

	public String getMethod() {
		return this.method;
	}

	public Map getParameters() {
		return this.parameters;
	}

	public String getPathInfo() {
		return this.pathInfo;
	}

	public String getQueryString() {
		return this.queryString;
	}

	public String getRequestURI() {
		return this.requestURI;
	}

	public String getServletPath() {
		return this.servletPath;
	}

}

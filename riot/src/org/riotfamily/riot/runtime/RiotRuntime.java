package org.riotfamily.riot.runtime;

import javax.servlet.ServletContext;

import org.riotfamily.riot.RiotVersion;
import org.springframework.util.Assert;
import org.springframework.web.context.ServletContextAware;

/**
 * Bean that exposes the riot-servlet prefix, the resource path and the
 * riot version.
 * <p>
 * By default, Riot assumes that the riot-servlet is mapped to 
 * <code>/riot/*</code>. In order to use a different mapping, you have to set 
 * the context attribute <code>riotServletPrefix</code> in your 
 * <code>web.xml</code>.
 * </p>
 */
public class RiotRuntime implements ServletContextAware {

	public static final String SERVLET_PREFIX_ATTRIBUTE = "riotServletPrefix";
	
	public static final String DEFAULT_SERVLET_PREFIX = "/riot";
	
	private String servletPrefix;
	
	private String resourceMapping;

	private String resourcePath;

	public void setResourceMapping(String resourceMapping) {
		this.resourceMapping = resourceMapping;
	}

	public void setServletContext(ServletContext context) {
		Assert.notNull(resourceMapping, "A resourceMapping must be specified.");
		servletPrefix = (String) context.getAttribute(SERVLET_PREFIX_ATTRIBUTE);
		if (servletPrefix == null) {
			servletPrefix = DEFAULT_SERVLET_PREFIX;
		}
		resourcePath = servletPrefix + resourceMapping + '/' + getVersionString();
	}
	
	public String getServletPrefix() {
		return servletPrefix;
	}
	
	public String getResourcePath() {
		return resourcePath;
	}
    
	public String getVersionString() {
		return RiotVersion.getVersionString();
	}

}

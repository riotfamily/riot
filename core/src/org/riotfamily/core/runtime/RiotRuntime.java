package org.riotfamily.core.runtime;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
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
	
	public static final String DEFAULT_RESOURCE_MAPPING = "/resources";
	
	private String resourceMapping = DEFAULT_RESOURCE_MAPPING;
	
	private String servletPrefix;
	
	private String resourcePath;
	
	
	public void setResourceMapping(String resourceMapping) {
		this.resourceMapping = resourceMapping;
	}
	
	public void setServletContext(ServletContext context) {
		Assert.notNull(resourceMapping, "A resourceMapping must be specified.");
		servletPrefix = (String) context.getInitParameter(SERVLET_PREFIX_ATTRIBUTE);
		if (servletPrefix == null) {
			servletPrefix = DEFAULT_SERVLET_PREFIX;
		}
		resourcePath = servletPrefix + resourceMapping + '/' + getRiotVersion() + '/';
	}
		
	public String getServletPrefix() {
		return servletPrefix;
	}
	
	public String getResourcePath() {
		return resourcePath;
	}
    
	private String getRiotVersion() {
		return RiotVersion.getVersionString();
	}	
	
	public static RiotRuntime getRuntime(ApplicationContext context) {
		return (RiotRuntime) BeanFactoryUtils.beanOfTypeIncludingAncestors(
				context, RiotRuntime.class);
	}
}

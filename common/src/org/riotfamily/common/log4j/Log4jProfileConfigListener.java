package org.riotfamily.common.log4j;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.util.ClassUtils;
import org.springframework.util.Log4jConfigurer;

/**
 * ServletContextListener that configures the Log4J logging system depending on
 * the environment where the application is deployed.
 * <p>
 * The listener looks for a log4j.properties or log4j.xml file in the classpath
 * under <code>profiles/&lt;profile&gt;</code>, where <i>profile</i> can be set
 * via a Java system property (e.g. <code>-Dprofile=foo</code>).
 *   
 * @author Felix Gnass [fgnass at neteye dot de]
 * @author Alf Werder [alf dot werder at artundweise dot de]
 */
public class Log4jProfileConfigListener implements ServletContextListener {

	private ServletContext servletContext;
	
	public void contextInitialized(ServletContextEvent event) {
		this.servletContext = event.getServletContext();
		initLogging();
	}

	public void contextDestroyed(ServletContextEvent event) {
		ServletContext context = event.getServletContext();
		context.log("Shutting down log4j");
		Log4jConfigurer.shutdownLogging();
	}
	
	protected ServletContext getServletContext() {
		return servletContext;
	}
	
	protected void initLogging() {
		initLogging(getProfile());
	}
	
	protected String getProfile() {
		String profile = System.getProperty("profile");
		if (profile == null) {
			try {
				profile = InetAddress.getLocalHost().getHostName();
			} 
			catch (UnknownHostException e) {
				throw new IllegalStateException(e);
			}
		}
		return profile;
	}

	protected void initLogging(String profile) {
		if (!configure(profile)) {
			if (!configure("_default")) {
				servletContext.log("No log4j configuration found in classpath:/conf/<profile>");	
			}
		}
	}
	
	protected boolean configure(String profile) {
		String resource = getConfigResource(profile, "log4j.xml");
		URL url = getResourceUrl(resource);
		if (url != null) {
			servletContext.log("Initializing log4j from [" + resource + "]");
			DOMConfigurator.configure(url);
			return true;
		}
		resource = getConfigResource(profile, "log4j.properties");
		url = getResourceUrl(resource);
		if (url != null) {
			servletContext.log("Initializing log4j from [" + resource + "]");
			PropertyConfigurator.configure(url);
			return true;
		}
		return false;
	}
	
	protected String getConfigResource(String profile, String fileName) {
		return "profiles/" + profile + "/" + fileName;
	}
	
	protected URL getResourceUrl(String resource) {
		return ClassUtils.getDefaultClassLoader().getResource(resource);
	}
	
}

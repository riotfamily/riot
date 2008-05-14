package org.riotfamily.common.beans.config;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * FactoryBean that imports a bean from another WebApplicationContext.
 * <p>
 * In order to work, the other context must be initialized before this bean is 
 * processed. You can control the initialization order via the 
 * <code>&lt;load-on-startup&gt;</code> tag in the web.xml descriptor.  
 * </p>
 * @since 7.0.1
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class WebApplicationContextBeanImporter implements ServletContextAware, 
		BeanNameAware, FactoryBean, InitializingBean {

	private String servletName;
	
	private String beanName;
	
	private ServletContext servletContext;
	
	private Object bean;
	
	/**
	 * Sets the name of the DispatcherServlet from which the bean should be imported.
	 */
	public void setServletName(String servletName) {
		this.servletName = servletName;
	}
	
	/**
	 * Sets the name of the bean to be imported. If not specified, the name
	 * defaults to the local bean name.
	 */
	public void setBeanName(String beanName) {
		if (this.beanName == null) {
			this.beanName = beanName;
		}
	}
	
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	public void afterPropertiesSet() throws Exception {
		bean = getWebsiteApplicationContext().getBean(beanName);
	}
	
	private WebApplicationContext getWebsiteApplicationContext() {
		Assert.notNull(servletName, "A servleName must be specified");
		String contextAttribute = DispatcherServlet.SERVLET_CONTEXT_PREFIX 
				+ servletName;
		
		WebApplicationContext ctx = (WebApplicationContext) 
				servletContext.getAttribute(contextAttribute);
		
		Assert.state(ctx != null, "No WebApplicationContext found in the " +
				"ServletContext under the key '" + contextAttribute + "'. " +
				"Make sure your DispatcherServlet is called '" + 
				servletName + "' and publishContext is set to true.");
		
		return ctx;
	}
	
	public Object getObject() throws Exception {
		return bean;
	}

	public Class getObjectType() {
		return null;
	}

	public boolean isSingleton() {
		return true;
	}

}

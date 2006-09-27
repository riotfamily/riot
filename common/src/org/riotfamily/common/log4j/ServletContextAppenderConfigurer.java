package org.riotfamily.common.log4j;

import javax.servlet.ServletContext;

import org.springframework.web.context.ServletContextAware;

/**
 * Bean that initializes the ServletContextAppender by setting a reference
 * to the ServletContext.
 */
public class ServletContextAppenderConfigurer implements ServletContextAware {

	public void setServletContext(ServletContext servletContext) {
		ServletContextAppender.setContext(servletContext);
	}

}

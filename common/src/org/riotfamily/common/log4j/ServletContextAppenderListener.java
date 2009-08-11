package org.riotfamily.common.log4j;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * ServletContextListener that initializes the ServletContextAppender by 
 * setting a reference to the ServletContext as soon as the context is
 * initialized.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class ServletContextAppenderListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent event) {
        ServletContextAppender.setContext(event.getServletContext());
    }

    public void contextDestroyed(final ServletContextEvent event) {
        ServletContextAppender.setContext(null);
    }

    
}
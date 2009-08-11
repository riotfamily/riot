package org.riotfamily.common.log4j;

import javax.servlet.ServletContext;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Log4J Appender implementation that logs messages by calling 
 * {@link javax.servlet.ServletContext#log(java.lang.String)
 * ServletContext.log()}. 
 * 
 * NOTE: Since the appender needs a reference to the ServletContext you must 
 * either add the {@link ServletContextAppenderListener} to your web.xml or 
 * put a {@link ServletContextAppenderConfigurer} into your ApplicationContext.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class ServletContextAppender extends AppenderSkeleton {

    protected static ServletContext servletContext;

    public static void setContext(ServletContext context) {
        servletContext = context;
    }

    protected void append(final LoggingEvent event) {
    	String msg = layout.format(event);
        if (servletContext == null) {
            System.out.println(msg);
        }
        else {
        	servletContext.log(msg);
        }
    }

    public boolean requiresLayout() {
        return true;
    }

    public void close() {
    }
}
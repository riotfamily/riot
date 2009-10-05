/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
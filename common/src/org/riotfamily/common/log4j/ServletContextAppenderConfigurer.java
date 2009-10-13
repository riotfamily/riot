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

import org.springframework.web.context.ServletContextAware;

/**
 * Bean that initializes the ServletContextAppender by setting a reference
 * to the ServletContext.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class ServletContextAppenderConfigurer implements ServletContextAware {

	public void setServletContext(ServletContext servletContext) {
		ServletContextAppender.setContext(servletContext);
	}

}

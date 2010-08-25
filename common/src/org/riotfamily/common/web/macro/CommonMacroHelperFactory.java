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
package org.riotfamily.common.web.macro;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.mvc.mapping.HandlerUrlResolver;
import org.riotfamily.common.web.performance.ResourceStamper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.ServletContextAware;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class CommonMacroHelperFactory implements MacroHelperFactory, 
		ApplicationContextAware, ServletContextAware {

	private ApplicationContext applicationContext;

	private ServletContext servletContext;
	
	private ResourceStamper stamper;
	
	private HandlerUrlResolver handlerUrlResolver;
		
	private boolean compressResources = false;
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	public void setStamper(ResourceStamper stamper) {
		this.stamper = stamper;
	}
	
	public void setHandlerUrlResolver(HandlerUrlResolver handlerUrlResolver) {
		this.handlerUrlResolver = handlerUrlResolver;
	}
	
	public void setCompressResources(boolean compressResources) {
		this.compressResources = compressResources;
	}
	
	public Object createMacroHelper(HttpServletRequest request, 
			HttpServletResponse response, Map<String, ?> model) {
		
		return new CommonMacroHelper(applicationContext, request, response, 
				servletContext, stamper, handlerUrlResolver, compressResources);
	}

}

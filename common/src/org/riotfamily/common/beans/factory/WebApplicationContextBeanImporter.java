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
package org.riotfamily.common.beans.factory;

import javax.servlet.ServletContext;

import org.riotfamily.common.util.SpringUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.context.ServletContextAware;

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
	@Required
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
		bean = SpringUtils.getBean(servletContext, servletName, beanName);
	}
		
	public Object getObject() throws Exception {
		return bean;
	}

	public Class<?> getObjectType() {
		return null;
	}

	public boolean isSingleton() {
		return true;
	}
	
}

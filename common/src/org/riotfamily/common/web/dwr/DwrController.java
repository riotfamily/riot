/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.web.dwr;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.directwebremoting.WebContextFactory.WebContextBuilder;
import org.directwebremoting.impl.ContainerUtil;
import org.directwebremoting.impl.StartupUtil;
import org.directwebremoting.servlet.UrlProcessor;
import org.directwebremoting.spring.SpringContainer;
import org.directwebremoting.util.FakeServletConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class DwrController extends AbstractController 
		implements BeanNameAware, InitializingBean, BeanFactoryAware, 
        ServletContextAware {

    private String beanName;
     
	private String mapping;
	
    private List configurators;
    
    private boolean includeDefaultConfig = true;
    
    private ServletConfig servletConfig;
    
    private Map parameters;
    
    private SpringContainer container;
    
    protected WebContextBuilder webContextBuilder;
    
	public void setMapping(String mapping) {
		this.mapping = mapping;
	}
	
	public void setBeanName(String beanName) {
		this.beanName = beanName;
		if (mapping == null) {
			mapping = beanName;
		}
	}
    
    /**
     * Is called by the Spring container to set the bean factory.
     * This bean factory is then used to obtain the global DWR configuration 
     * from. This global configuration is optional as DWR will provide defaults 
     * where possible.
     */
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        container = new SpringContainer();
        container.setBeanFactory(beanFactory);
    }
    
    /**
     * Sets the configurators to apply to this controller. The configurators 
     * are used to set up DWR correctly.
     */
    public void setConfigurators(List configurators) {
        this.configurators = configurators;
    }
    
    /**
	 * Sets parameters just like the init-parameters of the DwrServlet.
	 */
	public void setParameters(Map parameters) {
		this.parameters = parameters;
	}
    
    /**
     * Sets whether the default DWR configuration should be included 
     * (default is <code>true</code>).
     * <p>
     * This default configuration contains all build-in creators and converters.
     * You normally want this default configuration to be included.
     */
    public void setIncludeDefaultConfig(boolean includeDefaultConfig) {
        this.includeDefaultConfig = includeDefaultConfig;
    }
	
    /**
     * Is called by the Spring container after all properties have been set.
     * This method actually makes sure the container is correctly initialized 
     * and all configurators are processed.
     * 
     * @throws Exception in case setting up fails
     */
    public void afterPropertiesSet() throws Exception {
        ServletContext servletContext = getServletContext();
        Assert.notNull(configurators, "The 'configurators' property must be set");
        servletConfig = new FakeServletConfig(beanName, servletContext, parameters);

        try {
            ContainerUtil.setupDefaults(container);
            ContainerUtil.setupFromServletConfig(container, servletConfig);
            container.setupFinished();

            webContextBuilder = StartupUtil.initWebContext(servletConfig, servletContext, null, container);
            StartupUtil.initServerContext(servletConfig, servletContext, container);

            if (includeDefaultConfig) {
                ContainerUtil.configureFromSystemDwrXml(container);
            }

            ContainerUtil.configure(container, configurators);
            ContainerUtil.publishContainer(container, servletConfig);
        }
        finally {
            webContextBuilder.unset();
        }
    }

    /**
     * Handles all request to this controller.
     * <p>
     * It delegates to the <code>UrlProcessor</code> and also takes care of 
     * setting and unsetting of the current <code>WebContext</code>.
     */
    protected ModelAndView handleRequestInternal(HttpServletRequest request, 
            HttpServletResponse response) throws Exception  {

        HttpServletRequest shiftedRequest = new PathShiftingRequestWrapper(
                request, getPathOffset(request));
        
        try {
            // Set up the web context and delegate to the processor
            webContextBuilder.set(shiftedRequest, response, servletConfig, 
            		getServletContext(), container);

            UrlProcessor processor = (UrlProcessor) 
            		container.getBean(UrlProcessor.class.getName());
            
            processor.handle(shiftedRequest, response);
        }
        finally {
            webContextBuilder.unset();
        }

        return null;
    }
	
	protected int getPathOffset(HttpServletRequest request) {
		String pathInfo = request.getPathInfo();
		int i = pathInfo.indexOf(mapping);
		return i + mapping.length();
	}

}

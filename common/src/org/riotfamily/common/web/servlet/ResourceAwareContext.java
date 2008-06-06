package org.riotfamily.common.web.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.Resource;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.xml.sax.InputSource;

/**
 * XmlWebApplicationContext that is aware of all resources that are used
 * to configure the context, including resources referenced via 
 * <code>&lt;import resource="..." /&gt;</code> tags.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class ResourceAwareContext extends XmlWebApplicationContext {

	private ArrayList<Resource> configResources;
	
	protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) 
			throws IOException {
		
		configResources = new ArrayList<Resource>();
		
		XmlBeanDefinitionReader beanDefinitionReader = 
				new XmlBeanDefinitionReader(beanFactory) {
			
			protected int doLoadBeanDefinitions(InputSource inputSource, 
					Resource resource) throws BeanDefinitionStoreException {
				
				configResources.add(resource);
				return super.doLoadBeanDefinitions(inputSource, resource);
			}
		};

		beanDefinitionReader.setResourceLoader(this);
		beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this));
		initBeanDefinitionReader(beanDefinitionReader);
		loadBeanDefinitions(beanDefinitionReader);
	}
	
	public List<Resource> getConfigResources() {
		return configResources;
	}
	
}

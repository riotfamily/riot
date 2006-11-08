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

public class ResourceAwareContext extends XmlWebApplicationContext {

	private ArrayList configResources;
	
	protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) 
			throws IOException {
		
		configResources = new ArrayList();
		
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
	
	public List getConfigResources() {
		return configResources;
	}
	
}

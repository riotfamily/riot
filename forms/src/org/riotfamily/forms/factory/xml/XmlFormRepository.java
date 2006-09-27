package org.riotfamily.forms.factory.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.xml.ConfigurableBean;
import org.riotfamily.common.xml.XmlBeanConfigurer;
import org.riotfamily.forms.factory.FormFactory;
import org.riotfamily.forms.factory.support.AbstractFormRepository;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.io.Resource;

/**
 *
 */
public class XmlFormRepository extends AbstractFormRepository implements
		BeanFactoryAware, ApplicationEventPublisherAware, 
		InitializingBean, ConfigurableBean {

	private Log log = LogFactory.getLog(XmlFormRepository.class);
	
	private List configLocations;
	
	private boolean reloadable = true;

	private XmlBeanConfigurer configurer;
	
	private BeanFactory beanFactory;
		
	private Class defaultBeanClass;
	
	private ApplicationEventPublisher eventPublisher;

	private MimetypesFileTypeMap mimetypesMap;
	
	public void setConfig(Resource config) {
		this.configLocations = Collections.singletonList(config);
	}
	
	public void setConfigLocations(Resource[] configLocations) {
		this.configLocations = new ArrayList();
		if (configLocations != null) {
			for (int i = 0; i < configLocations.length; i++) {
				this.configLocations.add(configLocations[i]);
			}
		}
	}

	public Class getDefaultBeanClass() {
		return this.defaultBeanClass;
	}

	public void setDefaultBeanClass(Class defaultBeanClass) {
		this.defaultBeanClass = defaultBeanClass;
	}

	public boolean isReloadable() {
		return this.reloadable;
	}

	public void setReloadable(boolean reloadable) {
		if (!reloadable) {
			log.info("Modification checks have been disabled.");
		}
		this.reloadable = reloadable;
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
	
	public void setApplicationEventPublisher(
			ApplicationEventPublisher eventPublisher) {
		
		this.eventPublisher = eventPublisher;
	}

	public void setMimetypesMap(MimetypesFileTypeMap mimetypesMap) {
		this.mimetypesMap = mimetypesMap;
	}
	
	public MimetypesFileTypeMap getMimetypesMap() {
		return this.mimetypesMap;
	}

	public final void afterPropertiesSet() throws Exception {
		XmlFormRepositoryDigester digester = new XmlFormRepositoryDigester(
				this, beanFactory);
		
		configurer = new XmlBeanConfigurer(this, configLocations, digester, 
				eventPublisher);
		
		configurer.configure();
	}

	public FormFactory getFormFactory(String id) {
		configurer.checkForModifications();
		return super.getFormFactory(id);
	}
	
	public void reset() {
		getFactories().clear();
	}
	
	public void configured() {
	}

}

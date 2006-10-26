package org.riotfamily.forms.factory.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.xml.BeanConfigurationWatcher;
import org.riotfamily.common.xml.ConfigurableBean;
import org.riotfamily.common.xml.ConfigurationEventListener;
import org.riotfamily.common.xml.DocumentReader;
import org.riotfamily.common.xml.ValidatingDocumentReader;
import org.riotfamily.forms.factory.FormFactory;
import org.riotfamily.forms.factory.support.AbstractFormRepository;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

/**
 *
 */
public class XmlFormRepository extends AbstractFormRepository implements
		BeanFactoryAware, InitializingBean, ConfigurableBean {

	private Log log = LogFactory.getLog(XmlFormRepository.class);
	
	private List configLocations;
	
	private boolean reloadable = true;

	private BeanConfigurationWatcher configWatcher = 
			new BeanConfigurationWatcher(this);

	private XmlFormRepositoryDigester digester;
	
	private BeanFactory beanFactory;
		
	private Class defaultBeanClass;
	
	private MimetypesFileTypeMap mimetypesMap;
	
	public void setConfig(Resource config) {
		setConfigLocations(new Resource[] { config });
	}
	
	public void setConfigLocations(Resource[] configLocations) {
		this.configLocations = new ArrayList();
		if (configLocations != null) {
			for (int i = 0; i < configLocations.length; i++) {
				this.configLocations.add(configLocations[i]);
			}
		}
		configWatcher.setResources(this.configLocations);
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
	
	public void addListener(ConfigurationEventListener listener) {
		configWatcher.addListener(listener);
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
	
	public void setMimetypesMap(MimetypesFileTypeMap mimetypesMap) {
		this.mimetypesMap = mimetypesMap;
	}
	
	public MimetypesFileTypeMap getMimetypesMap() {
		return this.mimetypesMap;
	}

	public final void afterPropertiesSet() throws Exception {
		digester = new XmlFormRepositoryDigester(this, beanFactory);
		configure();
	}

	public FormFactory getFormFactory(String id) {
		configWatcher.checkForModifications();
		return super.getFormFactory(id);
	}
	
	public void configure() {
		getFactories().clear();
		Iterator it = configLocations.iterator();
		while (it.hasNext()) {
			Resource res = (Resource) it.next();
			DocumentReader reader = new ValidatingDocumentReader(res);
			digester.digest(reader.readDocument(), res);
		}
	}
	
}

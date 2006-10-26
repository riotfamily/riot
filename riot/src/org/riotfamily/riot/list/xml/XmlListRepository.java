package org.riotfamily.riot.list.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.riotfamily.common.xml.BeanConfigurationWatcher;
import org.riotfamily.common.xml.ConfigurableBean;
import org.riotfamily.common.xml.ConfigurationEventListener;
import org.riotfamily.common.xml.DocumentReader;
import org.riotfamily.common.xml.ValidatingDocumentReader;
import org.riotfamily.riot.list.ListConfig;
import org.riotfamily.riot.list.ListRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

/**
 *
 */
public class XmlListRepository extends ListRepository implements 
		InitializingBean, ConfigurableBean {

	private List configLocations;
	
	private boolean reloadable = true;
	
	private BeanConfigurationWatcher configWatcher;
	
	private XmlListRepositoryDigester digester;
	
	public XmlListRepository() {
		configWatcher = new BeanConfigurationWatcher(this);
	}
	
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

	public boolean isReloadable() {
		return this.reloadable;
	}

	public void setReloadable(boolean reloadable) {
		this.reloadable = reloadable;
	}

	public void addListener(ConfigurationEventListener listener) {
		configWatcher.addListener(listener);
	}
	
	public void afterPropertiesSet() throws Exception {
		digester = new XmlListRepositoryDigester(this, getApplicationContext());
		configure();
	}
	
	public ListConfig getListConfig(String listId) {
		configWatcher.checkForModifications();
		return super.getListConfig(listId);
	}
	
	public void configure() {
		getListConfigs().clear();
		Iterator it = configLocations.iterator();
		while (it.hasNext()) {
			Resource res = (Resource) it.next();
			DocumentReader reader = new ValidatingDocumentReader(res);
			digester.digest(reader.readDocument(), res);
		}
	}
		
}

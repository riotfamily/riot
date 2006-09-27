package org.riotfamily.riot.list.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.riotfamily.common.xml.ConfigurableBean;
import org.riotfamily.common.xml.XmlBeanConfigurer;
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
	
	private XmlBeanConfigurer configurer;
	
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

	public boolean isReloadable() {
		return this.reloadable;
	}

	public void setReloadable(boolean reloadable) {
		this.reloadable = reloadable;
	}

	public void afterPropertiesSet() throws Exception {
		XmlListRepositoryDigester digester = new XmlListRepositoryDigester(
				this, getApplicationContext());
		
		configurer = new XmlBeanConfigurer(this, configLocations, digester, 
				getApplicationContext());
		
		configurer.configure();
	}
	
	public ListConfig getListConfig(String listId) {
		configurer.checkForModifications();
		return super.getListConfig(listId);
	}
	
	public void configured() {
	}
	
	public void reset() {
		getListConfigs().clear();
	}
		
}

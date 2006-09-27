package org.riotfamily.riot.editor.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.riotfamily.common.xml.ConfigurationReloadedEvent;
import org.riotfamily.common.xml.ConfigurableBean;
import org.riotfamily.common.xml.XmlBeanConfigurer;
import org.riotfamily.riot.editor.EditorDefinition;
import org.riotfamily.riot.editor.EditorRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;

/**
 *
 */
public class XmlEditorRepository extends EditorRepository 
		implements InitializingBean, ConfigurableBean, ApplicationListener {

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
		XmlEditorRepositoryDigester digester = 
				new XmlEditorRepositoryDigester(this, getApplicationContext());
		
		configurer = new XmlBeanConfigurer(this, configLocations, digester, 
				getApplicationContext());
		
		configurer.configure();
	}
	
	public synchronized EditorDefinition getEditorDefinition(String editorId) {
		configurer.checkForModifications();
		return super.getEditorDefinition(editorId);
	}
	
	public void reset() {
		setRootGroupDefinition(null);
		getEditorDefinitions().clear();
	}

	public void configured() {
	}
	
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ConfigurationReloadedEvent) {
			if (event.getSource() == getListRepository() 
					|| event.getSource() == getFormRepository()) {
				
				configurer.reconfigure();
			}
		}
	}
	 
}

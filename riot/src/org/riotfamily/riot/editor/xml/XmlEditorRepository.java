package org.riotfamily.riot.editor.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.riotfamily.common.xml.BeanConfigurationWatcher;
import org.riotfamily.common.xml.ConfigurableBean;
import org.riotfamily.common.xml.ConfigurationEventListener;
import org.riotfamily.common.xml.DocumentReader;
import org.riotfamily.common.xml.ValidatingDocumentReader;
import org.riotfamily.riot.editor.EditorDefinition;
import org.riotfamily.riot.editor.EditorRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

/**
 *
 */
public class XmlEditorRepository extends EditorRepository 
		implements InitializingBean, ConfigurableBean, 
		ConfigurationEventListener {

	private List configLocations;

	private boolean reloadable = true;
	
	private BeanConfigurationWatcher configWatcher =  
			new BeanConfigurationWatcher(this);
	
	private XmlEditorRepositoryDigester digester;
	
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

	public void afterPropertiesSet() throws Exception {
		digester = new XmlEditorRepositoryDigester(this, getApplicationContext());
		configure();
		getFormRepository().addListener(this);
		getListRepository().addListener(this);
	}
	
	public synchronized EditorDefinition getEditorDefinition(String editorId) {
		configWatcher.checkForModifications();
		return super.getEditorDefinition(editorId);
	}
	
	
	public void configure() {
		setRootGroupDefinition(null);
		getEditorDefinitions().clear();
		Iterator it = configLocations.iterator();
		while (it.hasNext()) {
			Resource res = (Resource) it.next();
			DocumentReader reader = new ValidatingDocumentReader(res);
			digester.digest(reader.readDocument(), res);
		}
	}
	
	public void beanReconfigured(ConfigurableBean bean) {
		configure();
	}
	 
}

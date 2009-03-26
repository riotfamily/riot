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
package org.riotfamily.forms.factory.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.activation.MimetypesFileTypeMap;

import org.riotfamily.common.util.Generics;
import org.riotfamily.common.util.RiotLog;
import org.riotfamily.common.xml.BeanConfigurationWatcher;
import org.riotfamily.common.xml.ConfigurableBean;
import org.riotfamily.common.xml.ConfigurationEventListener;
import org.riotfamily.common.xml.DocumentReader;
import org.riotfamily.common.xml.ValidatingDocumentReader;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.FormInitializer;
import org.riotfamily.forms.factory.AbstractFormRepository;
import org.riotfamily.forms.factory.DefaultFormFactory;
import org.riotfamily.forms.factory.FormFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.validation.Validator;

/**
 *
 */
public class XmlFormRepository extends AbstractFormRepository implements
		BeanFactoryAware, InitializingBean, ConfigurableBean {

	private RiotLog log = RiotLog.get(XmlFormRepository.class);
	
	private List<Resource> configLocations;
	
	private List<Resource> priorityConfigLocations;
	
	private boolean reloadable = true;

	private BeanConfigurationWatcher configWatcher = 
			new BeanConfigurationWatcher(this);

	private XmlFormRepositoryDigester digester;
	
	private ConfigurableListableBeanFactory beanFactory;
		
	private Class<?> defaultBeanClass;
	
	private MimetypesFileTypeMap mimetypesMap;
	
	private Map<String, Map<String, Object>> tinyMCEProfiles;
	
	private HashMap<String, Class<? extends Element>> customElements;
	
	private HashMap<String, Class<? extends Element>> customElementsWithoutNS;
	
	
	public void setCustomElements(Map<String, Class<? extends Element>> elements) 
			throws ClassNotFoundException {
		
		customElements = new HashMap<String, Class<? extends Element>>();
		customElementsWithoutNS = new HashMap<String, Class<? extends Element>>();
		
		for (Entry<String, Class<? extends Element>> entry : elements.entrySet()) {
			if (entry.getValue() != null) {
				String type = entry.getKey();
				customElements.put(type, entry.getValue());
				int i = type.indexOf('}');
				if (i != -1 && i < type.length() - 1) {
					type = type.substring(i + 1);
					customElementsWithoutNS.put(type, entry.getValue());
				}
			}
		}
	}

	public Class<? extends Element> getElementClass(String type) {
		if (customElements == null) {
			return null;
		}
		Class<? extends Element> elementClass = customElements.get(type);
		if (elementClass == null) {
			elementClass = customElementsWithoutNS.get(type);
		}
		return elementClass;
	}
	
	public void setConfig(Resource config) {
		setConfigLocations(new Resource[] { config });
	}
	
	public void setConfigLocations(Resource[] configLocations) {
		this.configLocations = Generics.newArrayList();
		if (configLocations != null) {
			for (int i = 0; i < configLocations.length; i++) {
				this.configLocations.add(configLocations[i]);
			}
		}
		configWatcher.setResources(getConfigLocations());
	}

	public void setPriorityConfig(Resource config) {
		setPriorityConfigLocations(new Resource[] { config });
	}

	public void setPriorityConfigLocations(Resource[] configLocations) {
		this.priorityConfigLocations = Generics.newArrayList();
		if (configLocations != null) {
			for (int i = 0; i < configLocations.length; i++) {
				this.priorityConfigLocations.add(configLocations[i]);
			}
		}
		configWatcher.setResources(getConfigLocations());
	}
	
	private List<Resource> getConfigLocations() {
		ArrayList<Resource> mergedConfigLocations = Generics.newArrayList();
		if (configLocations != null) {
			mergedConfigLocations.addAll(configLocations);
		}
		if (priorityConfigLocations != null) {
			mergedConfigLocations.addAll(priorityConfigLocations);
			
		}
		return mergedConfigLocations;
	}
	
	public Class<?> getDefaultBeanClass() {
		return this.defaultBeanClass;
	}

	public void setDefaultBeanClass(Class<?> defaultBeanClass) {
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
		Assert.isInstanceOf(ConfigurableListableBeanFactory.class, beanFactory);
		this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
	}
	
	public void setMimetypesMap(MimetypesFileTypeMap mimetypesMap) {
		this.mimetypesMap = mimetypesMap;
	}
	
	public MimetypesFileTypeMap getMimetypesMap() {
		return this.mimetypesMap;
	}
	
	public Map<String, Object> getTinyMCEConfig(String profile) {
		if (profile == null) {
			profile = "default";
		}
		return tinyMCEProfiles.get(profile);
	}

	public void setTinyMCEProfiles(Map<String, Map<String, Object>> profiles) {
		this.tinyMCEProfiles = profiles;
	}

	public final void afterPropertiesSet() throws Exception {
		digester = new XmlFormRepositoryDigester(this, beanFactory);
		configure();
	}

	public FormFactory createFormFactory(Class<?> beanClass, 
			FormInitializer initializer, Validator validator) {
		
		return new DefaultFormFactory(initializer, validator, beanClass);
	}
	
	public FormFactory getFormFactory(String id) {
		configWatcher.checkForModifications();
		return super.getFormFactory(id);
	}
	
	public void registerImport(String formId, FormFactory importedFormFactory) {		
	}
	
	public void configure() {
		getFactories().clear();
		for (Resource res : getConfigLocations()) {
			if (res.exists()) {
				log.debug("Reading forms from " + res.getDescription());
				DocumentReader reader = new ValidatingDocumentReader(res);
				digester.digest(reader.readDocument(), res);
			}
		}
	}
	
}

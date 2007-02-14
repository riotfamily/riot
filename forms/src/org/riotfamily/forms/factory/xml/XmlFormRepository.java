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
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.forms.factory.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.xml.BeanConfigurationWatcher;
import org.riotfamily.common.xml.ConfigurableBean;
import org.riotfamily.common.xml.ConfigurationEventListener;
import org.riotfamily.common.xml.DocumentReader;
import org.riotfamily.common.xml.ValidatingDocumentReader;
import org.riotfamily.forms.element.support.image.ImageCropper;
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
	
	private ImageCropper imageCropper;
	
	private HashMap customElements;
	
	private HashMap customElementsWithoutNS;
	
	
	public void setCustomElements(Properties props) 
			throws ClassNotFoundException {
		
		customElements = new HashMap();
		customElementsWithoutNS = new HashMap();
		
		Iterator it = props.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry prop = (Map.Entry) it.next();
			String className = (String) prop.getValue();
			if (className != null) {
				String type = (String) prop.getKey();
				Class elementClass = Class.forName(className);
				customElements.put(type, elementClass);
				int i = type.indexOf('}');
				if (i != -1 && i < type.length() - 1) {
					type = type.substring(i + 1);
					customElementsWithoutNS.put(type, elementClass);
				}
			}
		}
	}

	public Class getElementClass(String type) {
		if (customElements == null) {
			return null;
		}
		Class elementClass = (Class) customElements.get(type);
		if (elementClass == null) {
			elementClass = (Class) customElementsWithoutNS.get(type);
		}
		return elementClass;
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
	
	public ImageCropper getImageCropper() {
		return this.imageCropper;
	}

	public void setImageCropper(ImageCropper imageCropper) {
		this.imageCropper = imageCropper;
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

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
package org.riotfamily.riot.list.xml;

import java.util.Iterator;
import java.util.List;

import org.riotfamily.common.util.Generics;
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

	private List<Resource> configLocations;
	
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
		this.configLocations = Generics.newArrayList();
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
		getListConfigsByClass().clear();
		Iterator<Resource> it = configLocations.iterator();
		while (it.hasNext()) {
			Resource res = it.next();
			DocumentReader reader = new ValidatingDocumentReader(res);
			digester.digest(reader.readDocument(), res);
		}
	}
		
}

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
package org.riotfamily.common.beans.module;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.MapFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Creates a map by merging the entries with the given key from all
 * {@link org.riotfamily.common.beans.module.FactoryBeanModule modules} found in
 * the ApplicationContext.
 * 
 * @see org.riotfamily.common.beans.module.FactoryBeanModule
 */
public class ModularMapFactoryBean extends MapFactoryBean 
		implements ApplicationContextAware {

	private static Log log = LogFactory.getLog(ModularMapFactoryBean.class);
	
	private String key;
	
	private boolean includeRootMap;
	
	private ApplicationContext applicationContext;
	
	public void setKey(String key) {
		this.key = key;
	}
	
	/**
	 * Sets whether the root bean defintion (having the key as id) should be
	 * included in the map. This is useful if not only modules but the
	 * application itself should be able to add entries to the map.
	 */
	public void setIncludeRootList(boolean includeRootMap) {
		this.includeRootMap = includeRootMap;
	}
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	protected Object createInstance() {
		Map result = (Map) super.createInstance();
		Collection modules =
			ModularFactoryBeansUtils.getFactoryBeanModules(applicationContext);
		
		Iterator it = modules.iterator();
		while (it.hasNext()) {
			FactoryBeanModule module = (FactoryBeanModule) it.next();
			Map moduleMap = module.getMap(key);
			if (moduleMap != null) {
				log.info("Adding entries defined by " + module.getName() 
						+ " to " + key);
				
				result.putAll(moduleMap);
			}
		}
		if (includeRootMap) {
			try {
				Map rootMap = (Map) applicationContext.getBean(key, Map.class);
				result.putAll(rootMap);
			}
			catch (NoSuchBeanDefinitionException e) {
			}
		}
		return result;
	}

}

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
package org.riotfamily.common.beans.module;

import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ModularPropertiesFactoryBean extends PropertiesFactoryBean
		implements ApplicationContextAware {

	private static Log log = LogFactory.getLog(ModularPropertiesFactoryBean.class);
	
	private String key;
	
	private ApplicationContext applicationContext;
		
	public void setKey(String key) {
		this.key = key;
	}
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	protected Object createInstance() throws IOException {
		Properties result = (Properties) super.createInstance();
		Collection modules =
			ModularFactoryBeansUtils.getFactoryBeanModules(applicationContext);
		
		Iterator it = modules.iterator();
		while (it.hasNext()) {
			FactoryBeanModule module = (FactoryBeanModule) it.next();
			Properties moduleProps = module.getProperties(key);
			if (moduleProps != null) {
				log.info("Adding entries defined by " 
						+ module.getName() + " to " + key);
				
				Enumeration en = moduleProps.propertyNames();
				while (en.hasMoreElements()) {
					String name = (String) en.nextElement();
					result.setProperty(name, moduleProps.getProperty(name));
				}
			}
		}
		return result;
	}

}

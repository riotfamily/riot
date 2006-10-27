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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.util.Assert;

/**
 * This class is used by Riot modules to define lists or maps that will be
 * merged with collections defined by other modules.
 *  
 * @see org.riotfamily.common.beans.module.ModularListFactoryBean
 * @see org.riotfamily.common.beans.module.ModularMapFactoryBean
 * @see org.riotfamily.common.beans.module.ModularPropertiesFactoryBean
 */
public class FactoryBeanModule implements BeanNameAware {

	private Map properties;
		
	private String beanName;

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public String getName() {
		return beanName;
	}

	public List getList(String key) {
		Object value = properties.get(key);
		if (value == null) {
			return null;
		}
		if (value instanceof List) {
			return (List) value;
		}
		else {
			return Collections.singletonList(value);
		}
	}
	
	public Map getMap(String key) {
		Object value = properties.get(key);
		if (value == null) {
			return null;
		}
		Assert.isInstanceOf(Map.class, value, "Module property [" + key 
				+ "] is not a java.util.Map: " + value.getClass().getName());
		
		return (Map) value;
	}
	
	public Properties getProperties(String key) {
		Object value = properties.get(key);
		if (value == null) {
			return null;
		}
		Assert.isInstanceOf(Properties.class, value, "Module property [" + key 
				+ "] is not a java.util.Properties: " 
				+ value.getClass().getName());
		
		return (Properties) value;
	}
	

	public void setProperties(Map properties) {
		this.properties = properties;
	}

}

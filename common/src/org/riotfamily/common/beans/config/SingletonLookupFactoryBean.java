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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.beans.config;

import java.util.Collection;

import org.riotfamily.common.util.SpringUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

/**
 * FactoryBean that looks up a bean of the specified type. 
 * An IllegalStateException is thrown if the ApplicationContext does not 
 * contain exactly one bean of the specified type.
 * <p>
 * Main purpose of this class is to get a reference to an anonymous 
 * bean exposed by a custom name space handler.
 * </p>
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class SingletonLookupFactoryBean implements FactoryBean, 
		ApplicationContextAware {

	private Class<?> objectType;
	
	private ApplicationContext applicationContext;
	
	public SingletonLookupFactoryBean(Class<?> objectType) {
		this.objectType = objectType;
	}

	public Class<?> getObjectType() {
		return null;
	}
	
	public boolean isSingleton() {
		return true;
	}
	
	public Object getObject() throws Exception {
		Collection<?> beans = SpringUtils.beansOfType(applicationContext, 
				objectType, false, false).values();
		
		Assert.isTrue(beans.size() == 1, "Expected exactly one bean of type [" 
				+ objectType + "] but found " + beans.size());
		
		return beans.iterator().next();
	}
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

}

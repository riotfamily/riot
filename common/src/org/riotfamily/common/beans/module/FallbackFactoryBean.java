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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public class FallbackFactoryBean implements FactoryBean, BeanFactoryAware, 
		BeanNameAware, InitializingBean {

	private static Log log = LogFactory.getLog(FallbackFactoryBean.class);
	
	public static final String IMPLEMENTATION_BEAN_NAME_SUFFIX = "Impl";
	
	private Object fallback;
	
	private String implementationBeanName;

	private Object implementation;
	
	private String beanName;

	private BeanFactory beanFactory;

	public void setImplementationBeanName(String implementationBeanName) {
		this.implementationBeanName = implementationBeanName;
	}

	public void setFallback(Object fallback) {
		this.fallback = fallback;
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(fallback, "A fallback implementation must be provided.");
		if (implementationBeanName == null) {
			implementationBeanName = beanName + IMPLEMENTATION_BEAN_NAME_SUFFIX;
		}
		if (beanFactory.containsBean(implementationBeanName)) {
			implementation = beanFactory.getBean(implementationBeanName);
			log.info(beanName + ": Using implementation '" 
					+ implementationBeanName + "' [" 
					+ implementation.getClass().getName() + "]");
		}
		else {
			implementation = fallback;
			log.info(beanName + ": Using fallback [" 
					+ implementation.getClass().getName() + "]");
		}
	}
	
	public Object getObject() throws Exception {
		return implementation;
	}

	public Class getObjectType() {
		return implementation != null ? implementation.getClass() : null;
	}

	public boolean isSingleton() {
		return true;
	}

}

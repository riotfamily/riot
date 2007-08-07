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
package org.riotfamily.common.beans.override;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * BeanFactoryPostProcessor that overrides properties of a bean that has been
 * defined elsewhere. You can use this class to customize beans defined by 
 * Riot modules without having to overwrite them completely. 
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class PropertyOverrideProcessor implements BeanFactoryPostProcessor {

	private static Log log = LogFactory.getLog(PropertyOverrideProcessor.class);
	
	private String ref;

	private PropertyValues propertyValues;
		
	public void setRef(String ref) {
		this.ref = ref;
	}

	public void setPropertyValues(PropertyValues propertyValues) {
		this.propertyValues = propertyValues;
	}
	

	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) 
			throws BeansException {
	
		log.info("Overriding properties of bean [" + ref + "]");
		BeanDefinition bd = beanFactory.getBeanDefinition(ref);
		bd.getPropertyValues().addPropertyValues(propertyValues);
	}
	
}

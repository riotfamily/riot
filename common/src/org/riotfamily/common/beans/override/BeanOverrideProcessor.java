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
import org.riotfamily.common.beans.override.OverrideNamespaceHandler.BeanReplacement;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.core.PriorityOrdered;

/**
 * BeanFactoryPostProcessor that can be used to replace beans that have been
 * defined elsewhere. Use this class when you want to write a Riot module that
 * needs to replace a bean which is provided by another module.
 * <p>Simply defining a bean with the same id would not work, because the order
 * in which the module configurations are processed is not defined.  
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class BeanOverrideProcessor implements BeanFactoryPostProcessor, PriorityOrdered {

	private Log log = LogFactory.getLog(BeanOverrideProcessor.class);
	
	private String ref;

	private BeanDefinition beanDefinition;
	
	private boolean merge;
	
	private int order = 1;
	
	public void setRef(String ref) {
		this.ref = ref;
	}

	public void setBeanDefinition(BeanDefinition beanDefinition) {
		this.beanDefinition = beanDefinition;
	}

	public void setBeanReplacement(BeanReplacement replacement) {
		setBeanDefinition(replacement.getBeanDefinition());
	}
	
	public void setMerge(boolean merge) {
		this.merge = merge;
	}
	
	public int getOrder() {
		return this.order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) 
			throws BeansException {
		
		BeanDefinition target = beanFactory.getBeanDefinition(ref);
		overwriteBeanDefinition(target, beanDefinition);
	}
	
	private void overwriteBeanDefinition(BeanDefinition target, BeanDefinition source) {
		log.debug("Replacing bean [" + ref + "] with a [" 
				+ source.getBeanClassName() + "]");
		
		target.setBeanClassName(source.getBeanClassName());
		ConstructorArgumentValues cas = target.getConstructorArgumentValues();
		cas.clear();
		cas.addArgumentValues(source.getConstructorArgumentValues());
		
		MutablePropertyValues pvs = target.getPropertyValues();
		if (!merge) {
			pvs.clear();
		}
		pvs.addPropertyValues(source.getPropertyValues());
	}
	
}

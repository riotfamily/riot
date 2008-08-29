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

import java.util.Map;

import org.riotfamily.common.log.RiotLog;
import org.riotfamily.common.log.RiotLog;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.MapFactoryBean;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class MapMergeProcessor implements BeanFactoryPostProcessor, PriorityOrdered {

	private RiotLog log = RiotLog.get(MapMergeProcessor.class);
			
	private String ref;
	
	private String property;
	
	private Map entries;
	
	private int order = 1;
	
	public int getOrder() {
		return this.order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
	
	public void setRef(String ref) {
		this.ref = ref;
	}
	
	public void setProperty(String property) {
		this.property = property;
	}

	public void setEntries(Map entries) {
		this.entries = entries;
	}

	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) 
			throws BeansException {

		BeanDefinition bd = beanFactory.getBeanDefinition(ref);
		if (property == null) {
			Assert.state(MapFactoryBean.class.getName().equals(bd.getBeanClassName()),
					"Bean [" + ref + "] must be a MapFactoryBean");
	
			property = "sourceMap";
		}
		
		if (log.isInfoEnabled()) {
			String keys = StringUtils.collectionToCommaDelimitedString(entries.keySet());
			log.debug("Adding [" + keys + "] to " + ref + "." + property);
		}
		
		PropertyValue pv = bd.getPropertyValues().getPropertyValue(property);
		if (pv == null) {
			// No map set on the target bean, create a new one ...
			ManagedMap map = new ManagedMap();
			map.putAll(entries);
			bd.getPropertyValues().addPropertyValue(property, map);
		}
		else {
			Object value = pv.getValue();
			if (value instanceof RuntimeBeanReference) {
				RuntimeBeanReference ref = (RuntimeBeanReference) value;
				value = beanFactory.getBean(ref.getBeanName());
			}
			Assert.isInstanceOf(Map.class, value);
			Map map = (Map) value;
			map.putAll(entries);
		}
	}
}

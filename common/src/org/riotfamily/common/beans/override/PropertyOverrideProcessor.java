/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.common.beans.override;

import org.riotfamily.common.util.RiotLog;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.PriorityOrdered;

/**
 * BeanFactoryPostProcessor that overrides properties of a bean that has been
 * defined elsewhere. You can use this class to customize beans defined by 
 * Riot modules without having to overwrite them completely. 
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class PropertyOverrideProcessor implements BeanFactoryPostProcessor, 
		PriorityOrdered {

	private RiotLog log = RiotLog.get(PropertyOverrideProcessor.class);
	
	private String ref;

	private PropertyValues propertyValues;
	
	private int order = 1;
		
	public void setRef(String ref) {
		this.ref = ref;
	}

	public void setPropertyValues(PropertyValues propertyValues) {
		this.propertyValues = propertyValues;
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
	
		log.debug("Overriding properties of bean [" + ref + "]");
		BeanDefinition bd = beanFactory.getBeanDefinition(ref);
		bd.getPropertyValues().addPropertyValues(propertyValues);
	}
	
}

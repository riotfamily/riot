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
package org.riotfamily.common.freemarker;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;

import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public class BeanFactoryTemplateModel implements TemplateHashModel {

	private BeanFactory beanFactory;
	
	private ObjectWrapper objectWrapper;
	
	
	public BeanFactoryTemplateModel(BeanFactory beanFactory,
			ObjectWrapper objectWrapper) {
		
		this.beanFactory = beanFactory;
		this.objectWrapper = objectWrapper;
	}

	public TemplateModel get(String key) throws TemplateModelException {
		Object bean = null;
		try {
			bean = beanFactory.getBean(key);
		}
		catch (BeansException e) {
			throw new TemplateModelException(e);
		}
		return objectWrapper.wrap(bean);
	}

	public boolean isEmpty() throws TemplateModelException {
		return false;
	}

}

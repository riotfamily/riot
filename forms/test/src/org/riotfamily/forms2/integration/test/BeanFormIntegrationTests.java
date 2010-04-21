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
package org.riotfamily.forms2.integration.test;

import javax.annotation.Resource;

import org.riotfamily.forms2.integration.model.TestBean;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

public class BeanFormIntegrationTests extends AbstractFormIntegrationTests {

	private BeanWrapper beanWrapper;
	
	@Resource
	public void setTestBean(TestBean testBean) {
		beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(testBean);
	}
	@Override
	protected String getControllerMapping() {
		return "form/bean";
	}

	@Override
	protected Object getFieldValue(String field) {
		return beanWrapper.getPropertyValue(field);
	}

}

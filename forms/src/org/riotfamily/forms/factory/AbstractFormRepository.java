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
package org.riotfamily.forms.factory;

import java.util.HashMap;
import java.util.Map.Entry;

import org.riotfamily.forms.Form;

public abstract class AbstractFormRepository implements FormRepository {

	private HashMap<String, FormFactory> factories = new HashMap<String, FormFactory>();

	public boolean containsForm(String id) {
		return factories.containsKey(id);
	}

	public FormFactory getFormFactory(String id) {
		FormFactory factory = (FormFactory) factories.get(id);
		if (factory == null) {
			throw new FormDefinitionException("No such form: " + id);
		}
		return factory;
	}

	public Form createForm(String id) {
		Form form = getFormFactory(id).createForm();
		form.setId(id);
		return form;
	}

	public Class<?> getBeanClass(String id) {
		return getFormFactory(id).getBeanClass();
	}
	
	public String findFormId(Class<?> beanClass) {
		for (Entry<String, FormFactory> entry : factories.entrySet()) {
			if (beanClass.equals(entry.getValue().getBeanClass())) {
				return entry.getKey();
			}
		}
		return null;
	}

	public void registerFormFactory(String id, FormFactory formFactory) {
		factories.put(id, formFactory);
	}

	protected HashMap<String, FormFactory> getFactories() {
		return this.factories;
	}

}

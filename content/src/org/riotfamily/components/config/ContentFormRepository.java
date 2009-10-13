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
package org.riotfamily.components.config;

import org.riotfamily.forms.FormInitializer;
import org.riotfamily.forms.MapEditorBinder;
import org.riotfamily.forms.factory.DefaultFormFactory;
import org.riotfamily.forms.factory.FormFactory;
import org.riotfamily.forms.factory.xml.XmlFormRepository;
import org.springframework.validation.Validator;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class ContentFormRepository extends XmlFormRepository {

	public FormFactory createFormFactory(Class<?> beanClass, 
			FormInitializer initializer, Validator validator) {
		
		if (beanClass == null) {
			DefaultFormFactory factory = new DefaultFormFactory(initializer, validator);
			factory.setEditorBinderClass(MapEditorBinder.class);
			return factory;
		}
		return super.createFormFactory(beanClass, initializer, validator);
	}
	
	public String getContentFormUrl(String formId, Long containerId, Long contentId) {
		if (containsForm(formId)) {
			return "/components/form/" + formId + "/" + containerId + "/" + contentId;
		}
		return null;
	}
	
}

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
package org.riotfamily.pages.riot.form;

import org.riotfamily.forms.Form;
import org.riotfamily.forms.FormInitializer;
import org.riotfamily.forms.factory.FormRepository;

/**
 * FormInitializer that imports form fields defined in content-forms.xml.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class SiteFormInitializer implements FormInitializer {

	private FormRepository repository;

	public SiteFormInitializer(FormRepository repository) {
		this.repository = repository;
	}

	public void initForm(Form form) {
		ContentForm editor = ContentForm.createIfExists("site", repository);
		if (editor != null) {
			form.addElement(editor, "properties");
		}
	}

}

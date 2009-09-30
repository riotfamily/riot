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

import org.riotfamily.components.model.Content;
import org.riotfamily.forms.ElementFactory;
import org.riotfamily.forms.MapEditorBinder;
import org.riotfamily.forms.element.NestedForm;
import org.riotfamily.forms.factory.FormFactory;
import org.riotfamily.forms.factory.FormRepository;

public class ContentForm extends NestedForm {
	
	private FormFactory factory;
	
	public ContentForm(FormFactory factory) {
		this.factory = factory;
		setEditorBinder(new MapEditorBinder(Content.class));
		setRequired(true);
		setIndent(false);
	}
	
	@Override
	protected void afterFormContextSet() {
		super.afterFormContextSet();
		for (ElementFactory ef : factory.getChildFactories()) {
			addElement(ef.createElement(this, getForm(), true));
		}
	}
	
	public static ContentForm createIfExists(String id, FormRepository repository) {
		if (repository.containsForm(id)) {
			FormFactory factory = repository.getFormFactory(id);
			return new ContentForm(factory);
		}
		return null;
	}

}

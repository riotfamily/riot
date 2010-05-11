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

import org.riotfamily.forms.base.Binding;
import org.riotfamily.forms.base.Element;
import org.riotfamily.forms.client.Html;
import org.riotfamily.forms.element.SwitchElement;
import org.riotfamily.forms.value.Value;
import org.riotfamily.pages.config.SitemapSchema;
import org.riotfamily.pages.config.SitemapSchemaRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class SitePropertiesElement extends Element {

	@Autowired
	private transient SitemapSchemaRepository schemaRepository;
	
	public class State extends Element.State {

		private Element.State switchState;
		
		@Override
		protected void onInit() {
			SwitchElement switchElement = new SwitchElement("schemaName");
			switchElement.setLabel("{.schema}");
			for (SitemapSchema schema : schemaRepository.getSchemas()) {
				switchElement.addCase(schema.getLabel(), schema.getName(), 
						new Binding("properties", schema.getForm()).omitLabel());
			}
			switchState = switchElement.createState(this);
		}
		
		@Override
		protected void renderElement(Html html) {
			switchState.render(html); //REVISIT Overwrite render instead?
		}
		
		@Override
		public void setValue(Object value) {
			switchState.setValue(value);
		}
		
		@Override
		public void populate(Value value) {
			switchState.populate(value);
		}
		
	}
}

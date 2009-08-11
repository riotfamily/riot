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

import org.riotfamily.forms.ElementFactory;
import org.riotfamily.pages.model.Site;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class SitePropertyElement extends AbstractLocalizedElement {

	private Site masterSite;
	
	public SitePropertyElement(ElementFactory elementFactory,
			LocalizedEditorBinder binder, Site masterSite) {
		
		super(elementFactory, binder);
		this.masterSite = masterSite;
	}

	protected boolean isLocalized() {
		return masterSite != null;
	}
	
	protected Object getMasterValue(String property) {
		if (masterSite.getProperties() != null) {
			return masterSite.getProperties().get(property);
		}
		return null;
	}
	
}

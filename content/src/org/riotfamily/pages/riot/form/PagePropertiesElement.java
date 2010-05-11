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

import java.util.Collection;
import java.util.Collections;

import org.riotfamily.core.screen.ScreenUtils;
import org.riotfamily.forms.base.Binding;
import org.riotfamily.forms.base.Element;
import org.riotfamily.forms.client.Html;
import org.riotfamily.forms.element.SwitchElement;
import org.riotfamily.forms.value.Value;
import org.riotfamily.pages.config.PageType;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;

public class PagePropertiesElement extends Element {

	public class State extends Element.State {

		private Element.State switchState;
		
		@Override
		protected void onInit() {
			SwitchElement switchElement = new SwitchElement("pageTypeName");
			switchElement.setLabel("{pageType}");
			for (PageType pageType : getPageTypes()) {
				switchElement.addCase(pageType.getLabel(), pageType.getName(), 
						new Binding("contentContainer.previewVersion", pageType.getForm()).omitLabel());
			}
			switchState = switchElement.createState(this);
		}
		
		private Collection<PageType> getPageTypes() {
			Object parent = ScreenUtils.getParentObjectOrNode();
			if (parent instanceof Page) {
				return ((Page) parent).getPageType().getChildTypes();
			}
			else if (parent instanceof Site) {
				return Collections.singleton(((Site) parent).getRootPage().getPageType());
			}
			throw new IllegalStateException("Parent must either be a Page or a Site");
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

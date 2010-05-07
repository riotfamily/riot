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

import org.riotfamily.core.screen.ScreenContext;
import org.riotfamily.core.screen.ScreenContextHolder;
import org.riotfamily.forms.base.Binding;
import org.riotfamily.forms.base.Element;
import org.riotfamily.forms.element.NestedForm;
import org.riotfamily.forms.element.SelectBox;
import org.riotfamily.forms.option.OptionsModel;
import org.riotfamily.pages.model.Page;

public class PagePropertiesElement extends Element {

	private SelectBox selectBox;
	
	public PagePropertiesElement() {
		selectBox = new SelectBox();
		new Binding("pageType", selectBox);
		new Binding("contentContainer.previewVersion", new NestedForm());
	}

	
	public class Foo implements OptionsModel {
		public Iterable<?> getOptions(Element.State state) { 
			ScreenContext screenContext = ScreenContextHolder.get();
			screenContext.getParent();
			screenContext.getObject();
			Page parent = null;
			return parent.getPageType().getChildTypes();
		}
	}
	/*
	public class State extends Element.State {
		
		private Element.State bindingState;
		
		private PropertyContainer.State containerState;
		
		@Override
		protected void onInit() {
			
		}
	}

	private class PropertyContainer extends Element {
		
		//private List<Case> cases = Generics.newArrayList();
		
		@Override
		public Collection<Element> getChildElements() {
			List<Element> elements = Generics.newArrayList();
			for (Case c : cases) {
				elements.add(c.getElement());
			}
			return elements;
		}
		
		public class State extends Element.State {
			
			private Map<String, Element.State> states = Generics.newHashMap();
			
			private Element.State activeState;
			
			@Override
			protected void onInit() {
				for (Case c : cases) {
					states.put(c.discriminator, c.element.createState(this));
				}
			}
			
			@Override
			public void setValue(Object value) {
				activeState.setValue(value);
			}
						
			public void activate(String discriminator) {
				activeState = states.get(discriminator);
			}
			
			@Override
			public void populate(Value value) {
				for (Element.State state : states.values()) {
					if (state != activeState) {
						state.setValue(null);
						state.populate(value);
					}
				}
				activeState.populate(value);
			}

			@Override
			protected void renderElement(Html html) {
				activeState.render(html);	
			}

		}
	}
*/
}

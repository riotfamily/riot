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
package org.riotfamily.forms2.element;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.riotfamily.common.util.Generics;
import org.riotfamily.forms2.base.Binding;
import org.riotfamily.forms2.base.Element;
import org.riotfamily.forms2.base.StateEvent;
import org.riotfamily.forms2.base.StateEventHandler;
import org.riotfamily.forms2.client.Html;
import org.riotfamily.forms2.option.OptionLabel;
import org.riotfamily.forms2.option.OptionValue;
import org.riotfamily.forms2.option.OptionsModel;
import org.riotfamily.forms2.value.Value;

public class SwitchElement extends Element {

	private SelectBox selectBox;
	
	private Binding binding;
	
	private CaseContainer caseContainer = new CaseContainer();
	
	public SwitchElement() {
		selectBox = new SelectBox(caseContainer);
	}
	
	@Override
	public Collection<Element> getChildElements() {
		return Arrays.asList(binding, caseContainer);
	}
	
	public SwitchElement(String discriminator) {
		this();
		setDiscriminator(discriminator);
	}
	
	public final void setDiscriminator(String name) {
		binding = new Binding(name, selectBox);
	}
	
	public SwitchElement addCase(Case c) {
		caseContainer.cases.add(c);
		return this;
	}
	
	public SwitchElement addCase(String discriminator, Element element) {
		return addCase(discriminator, discriminator, element);
	}
	
	public SwitchElement addCase(String label, String discriminator, Element element) {
		return addCase(new Case(label, discriminator, element));
	}
	
	public class State extends Element.State {

		private Element.State bindingState;
		
		private CaseContainer.State containerState;
		
		@Override
		protected void onInit() {
			bindingState = binding.createState(this);
			containerState = (CaseContainer.State) caseContainer.createState(this);
			bindingState.addStateEventHandler(new StateEventHandler() {
				public void handle(StateEvent event) {
					event.stop();
					containerState.activate((String) event.getSource().getValue());
					event.getUserInterface().refresh(containerState);
				}
			});
		}
		
		@Override
		public void setValue(Object value) {
			bindingState.setValue(value);
			containerState.activate((String) bindingState.getValue());
			containerState.setValue(value);
		}
		
		@Override
		public void populate(Value value) {
			bindingState.populate(value);
			containerState.populate(value);
		}

		@Override
		protected void renderElement(Html html) {
			bindingState.render(html);
			containerState.render(html);
		}
		
	}
	
	private class CaseContainer extends Element implements OptionsModel {
		
		private List<Case> cases = Generics.newArrayList();
		
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

		public Iterable<?> getOptions(Element.State state) {
			return cases;
		}
	}
	
	public class Case implements Serializable {
		
		private String label;
		
		private String discriminator;
		
		private Element element;

		public Case() {
		}
		
		public Case(String label, String discriminator, Element element) {
			this.label = label;
			this.discriminator = discriminator;
			this.element = element;
		}

		@OptionLabel
		public String getLabel() {
			return label;
		}

		@OptionValue
		public String getDiscriminator() {
			return discriminator;
		}

		public Element getElement() {
			return element;
		}

	}
}

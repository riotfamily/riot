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
package org.riotfamily.forms.element;

import java.util.List;
import java.util.Map;

import org.riotfamily.common.util.Generics;
import org.riotfamily.forms.base.Binding;
import org.riotfamily.forms.base.Element;
import org.riotfamily.forms.base.ElementWrapper;
import org.riotfamily.forms.base.StateEvent;
import org.riotfamily.forms.base.StateEventHandler;
import org.riotfamily.forms.client.Html;
import org.riotfamily.forms.option.OptionLabel;
import org.riotfamily.forms.option.OptionValue;
import org.riotfamily.forms.option.OptionsModel;
import org.riotfamily.forms.value.Value;

public class SwitchElement extends Element {

	private CaseContainer caseContainer;

	private SelectBox selectBox;
	
	private Binding binding;
	
	public SwitchElement() {
		caseContainer = new CaseContainer();
		selectBox = new SelectBox(caseContainer);
		binding = new Binding();
		binding.setElement(selectBox);
	}
		
	public SwitchElement(String discriminator) {
		this();
		setDiscriminator(discriminator);
	}
	
	public final void setDiscriminator(String name) {
		binding.setTarget(name);
	}
	
	public final void setLabel(String label) {
		binding.setLabel(label);
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
		protected void renderElement(Html html) {
			bindingState.render(html);
			containerState.render(html);
		}
		
	}
	
	private class CaseContainer extends Element implements OptionsModel {
		
		private List<Case> cases = Generics.newArrayList();
				
		public class State extends Element.State {
			
			private Map<String, Element.State> states = Generics.newHashMap();
			
			private Element.State activeState;
			
			@Override
			protected void onInit() {
				for (Case c : cases) {
					states.put(c.discriminator, c.getElement().createState(this));
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
	
	public class Case extends ElementWrapper {
		
		private String label;
		
		private String discriminator;
		
		public Case() {
		}
		
		public Case(String label, String discriminator, Element element) {
			super(element);
			this.label = label;
			this.discriminator = discriminator;
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
			return getWrappedElement();
		}

	}
}

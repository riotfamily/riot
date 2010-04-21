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
package org.riotfamily.forms2.element.support;

import java.io.Serializable;
import java.util.List;

import org.riotfamily.common.util.Generics;
import org.riotfamily.forms2.base.Element;
import org.riotfamily.forms2.base.ElementState;
import org.riotfamily.forms2.client.Html;
import org.riotfamily.forms2.option.IdentityReferenceAdapter;
import org.riotfamily.forms2.option.OptionReferenceAdapter;
import org.riotfamily.forms2.option.OptionsModel;
import org.riotfamily.forms2.value.Value;

public abstract class SelectElement extends Element {

	private transient OptionsModel optionsModel;
	
	private transient OptionReferenceAdapter referenceAdapter = new IdentityReferenceAdapter(); //TODO
	
	public OptionReferenceAdapter getReferenceAdapter() {
		return referenceAdapter;
	}
	
	public void setOptionsModel(OptionsModel optionsModel) {
		this.optionsModel = optionsModel;
	}

	public Object resolve(Serializable reference) {
		return referenceAdapter.resolve(reference);
	}
		
	protected abstract Class<?> getRequiredType();
	
	final void createOptions(State state, Object value) {
		Iterable<?> items = optionsModel.getOptions(state);
		if (items != null) {
			for (Object item : items) {
				state.addOption(
						referenceAdapter.createReference(item), 
						item.toString(),
						isSelected(item, value));
			}
		}
	}

	protected abstract boolean isSelected(Object option, Object value);
	
	protected abstract void buildOptionsDom(List<Option> options, Html html);
	
	abstract class State extends ElementState {

		protected List<Option> options = Generics.newArrayList();
			
		@Override
		protected void onInit(Value value) {
			createOptions(this, value.get());
		}
		
		public void addOption(Serializable reference, String label, boolean selected) {
			String value = String.valueOf(options.size());
			options.add(new Option(id(), reference, value, label, selected));
		}
		
		@Override
		protected void renderElement(Html html) {
			buildOptionsDom(options, html);
		}
		
	}
	
}

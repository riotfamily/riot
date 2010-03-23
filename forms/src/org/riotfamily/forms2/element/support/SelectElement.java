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

import org.riotfamily.common.ui.ObjectRenderer;
import org.riotfamily.forms2.base.Element;
import org.riotfamily.forms2.client.Html;
import org.riotfamily.forms2.option.IdentityReferenceAdapter;
import org.riotfamily.forms2.option.OptionReferenceAdapter;
import org.riotfamily.forms2.option.OptionsModel;

public abstract class SelectElement extends Element {

	private OptionsModel optionsModel;
	
	private ObjectRenderer labelRenderer;
	
	private OptionReferenceAdapter referenceAdapter = new IdentityReferenceAdapter(); //TODO
	
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
	
	final void createOptions(SelectionState state, Object value) {
		Iterable<?> items = optionsModel.getOptions(state.getFormState());
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
	
}

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
package org.riotfamily.forms.element.support;

import java.util.List;

import org.riotfamily.common.util.Generics;
import org.riotfamily.forms.base.Element;
import org.riotfamily.forms.client.Html;
import org.riotfamily.forms.option.DefaultOptionCreator;
import org.riotfamily.forms.option.Option;
import org.riotfamily.forms.option.OptionCreator;
import org.riotfamily.forms.option.OptionsModel;

public abstract class SelectElement extends Element {

	private OptionsModel optionsModel;
	
	private OptionCreator optionCreator = DefaultOptionCreator.defaultInstance();
	
	public void setOptionsModel(OptionsModel optionsModel) {
		this.optionsModel = optionsModel;
	}

	protected abstract Class<?> getRequiredType();
	
	abstract class State extends Element.State {

		protected List<OptionState> options = Generics.newArrayList();
			
		@Override
		public void setValue(Object value) {
			options.clear();
			createOptions(value);
		}
		
		private void createOptions(Object value) {
			Iterable<?> items = optionsModel.getOptions(this);
			if (items != null) {
				for (Object item : items) {
					Option option = optionCreator.createOption(item);
					options.add(new OptionState(
							id(), 
							getReferenceService().createReference(option.getValue()), 
							String.valueOf(options.size()), 
							renderLabel(option.getLabel()), 
							isSelected(option.getValue(), value)));
				}
			}
			postProcessOptions();
		}
		
		protected String renderLabel(Object label) {
			return getRenderingService().render(label);
		}
		
		protected abstract boolean isSelected(Object option, Object value);
		
		protected void postProcessOptions() {
		}
		
		protected boolean anyOptionSelected() {
			for (OptionState option : options) {
				if (option.isSelected()) {
					return true;
				}
			}
			return false;
		}
		
		@Override
		protected void renderElement(Html html) {
			buildOptionsDom(options, html);
		}
		
		protected abstract void buildOptionsDom(List<OptionState> options, Html html);
		
	}
	
}

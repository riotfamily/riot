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

import org.riotfamily.common.ui.RenderingService;
import org.riotfamily.common.util.Generics;
import org.riotfamily.forms2.base.Element;
import org.riotfamily.forms2.base.Element.State;
import org.riotfamily.forms2.client.Html;
import org.riotfamily.forms2.option.DefaultOptionCreator;
import org.riotfamily.forms2.option.IdentityReferenceAdapter;
import org.riotfamily.forms2.option.Option;
import org.riotfamily.forms2.option.OptionCreator;
import org.riotfamily.forms2.option.OptionsModel;
import org.riotfamily.forms2.option.ReferenceAdapter;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class SelectElement extends Element {

	private transient OptionsModel optionsModel;
	
	private transient ReferenceAdapter referenceAdapter = new IdentityReferenceAdapter(); //TODO Use ReferenceService or make this part of the Value class
	
	private transient RenderingService renderingService;
	
	private transient OptionCreator optionCreator = DefaultOptionCreator.defaultInstance();
	
	public ReferenceAdapter getReferenceAdapter() {
		return referenceAdapter;
	}
	
	public void setOptionsModel(OptionsModel optionsModel) {
		this.optionsModel = optionsModel;
	}

	@Autowired
	public void setRenderingService(RenderingService renderingService) {
		this.renderingService = renderingService;
	}
	
	public Object resolve(Serializable reference) {
		return referenceAdapter.resolve(reference);
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
							referenceAdapter.createReference(option.getValue()), 
							String.valueOf(options.size()), 
							renderLabel(option.getLabel()), 
							isSelected(option.getValue(), value)));
				}
			}
			postProcessOptions();
		}
		
		protected String renderLabel(Object label) {
			if (renderingService != null) {
				return renderingService.render(label);
			}
			return label != null ? label.toString() : "";
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

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
package org.riotfamily.forms.options;

import java.util.Collection;

import org.riotfamily.forms.Editor;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.OptionsModelAdapter;
import org.riotfamily.forms.element.select.SelectElement;
import org.riotfamily.forms.event.ChangeEvent;
import org.riotfamily.forms.event.ChangeListener;
import org.springframework.util.Assert;

public class DependentOptionsModelAdapter implements OptionsModelAdapter {

	public boolean supports(Object model) {
		return model instanceof DependentOptionsModel<?>;
	}
	
	@SuppressWarnings("unchecked")
	public OptionsModel adapt(Object model, Element element) {
		Assert.isInstanceOf(SelectElement.class, element);
		DependentOptionsModel dop = (DependentOptionsModel) model;
		Editor parent = element.getForm().getEditor(dop.getParentProperty());
		return new ChildOptionsModel(parent, (SelectElement) element, dop);
	}

	private static class ChildOptionsModel implements OptionsModel, ChangeListener {

		private Editor parent;
		
		private SelectElement child;
		
		private DependentOptionsModel<Object> dop;
		
		public ChildOptionsModel(Editor parent, SelectElement child, 
				DependentOptionsModel<Object> dop) {
			
			this.parent = parent;
			this.child = child;
			this.dop = dop;
			parent.addChangeListener(this);
		}

		public void valueChanged(ChangeEvent event) {
			child.reset();
		}
		
		public Collection<?> getOptionValues(Element element) {
			return dop.getOptionValues(parent.getValue()); 
		}
		
	}
}

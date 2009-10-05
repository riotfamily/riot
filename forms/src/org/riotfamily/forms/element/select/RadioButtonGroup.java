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
package org.riotfamily.forms.element.select;

import java.io.PrintWriter;
import java.util.HashMap;

import org.riotfamily.forms.TemplateUtils;

/**
 * Single-select element that uses a group of radio-buttons to render 
 * the options. Internally a template is used in order to allow the 
 * customization of the layout.
 */
public class RadioButtonGroup extends AbstractSingleSelectElement {

	private String template;
	
	public RadioButtonGroup() {
		setOptionRenderer(new InputTagRenderer("radio"));
		template = TemplateUtils.getTemplatePath(RadioButtonGroup.class);
	}
	
	public void setTemplate(String template) {
		this.template = template;
	}

	protected void renderInternal(PrintWriter writer) {
		HashMap<String, Object> model = new HashMap<String, Object>();
		model.put("element", this);
		model.put("options", getOptionItems());
		getFormContext().getTemplateRenderer().render(template, model, writer);
	}
	
	public boolean isCompositeElement() {
		return true;
	}

}
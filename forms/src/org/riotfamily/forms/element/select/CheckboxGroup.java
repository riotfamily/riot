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

import org.riotfamily.forms.DHTMLElement;
import org.riotfamily.forms.TemplateUtils;
import org.riotfamily.forms.resource.FormResource;
import org.riotfamily.forms.resource.ResourceElement;
import org.riotfamily.forms.resource.Resources;
import org.riotfamily.forms.resource.ScriptResource;
import org.riotfamily.forms.ui.Dimension;


/**
 * Multi-select element that uses a group of checkboxes to render the options.
 * Internally a template is used in order to allow the customization of
 * the layout.
 */
public class CheckboxGroup extends AbstractMultiSelectElement 
		implements DHTMLElement, ResourceElement {

	protected static final FormResource RESOURCE = new ScriptResource(
			"riot/checkbox.js", "RiotCheckboxGroup", Resources.PROTOTYPE);
	
	private String template;

	public CheckboxGroup() {
		setOptionRenderer(new InputTagRenderer("checkbox"));
		template = TemplateUtils.getTemplatePath(CheckboxGroup.class);
	}
	
	@Override
	public String getEventTriggerId() {		
		return getId();
	}

	public FormResource getResource() {
		return RESOURCE;
	}
	
	public void setTemplate(String template) {
		this.template = template;
	}

	@Override
	protected void renderInternal(PrintWriter writer) {
		HashMap<String, Object> model = new HashMap<String, Object>();
		model.put("element", this);
		model.put("options", getOptionItems());
		getFormContext().getTemplateRenderer().render(template, model, writer);
	}
	
	public Dimension getDimension() {
		return getFormContext().getSizing().getCheckboxSize().times(
				1, getOptionItems().size());
	}
		
	public String getInitScript() {
		return "new RiotCheckboxGroup('" + getEventTriggerId() + "');";
	}

	@Override
	public boolean isCompositeElement() {
		return true;
	}
}
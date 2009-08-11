package org.riotfamily.forms.element.select;

import java.io.PrintWriter;
import java.util.HashMap;

import org.riotfamily.forms.DHTMLElement;
import org.riotfamily.forms.TemplateUtils;
import org.riotfamily.forms.resource.FormResource;
import org.riotfamily.forms.resource.ResourceElement;
import org.riotfamily.forms.resource.Resources;
import org.riotfamily.forms.resource.ScriptResource;


/**
 * Multi-select element that uses a group of checkboxes to render the options.
 * Internally a template is used in order to allow the customization of
 * the layout.
 */
public class CheckboxGroup extends AbstractMultiSelectElement 
		implements DHTMLElement, ResourceElement {

	protected static final FormResource RESOURCE = new ScriptResource(
			"riot-js/checkbox.js", "RiotCheckboxGroup", Resources.PROTOTYPE);
	
	private String template;

	public CheckboxGroup() {
		setOptionRenderer(new InputTagRenderer("checkbox"));
		template = TemplateUtils.getTemplatePath(CheckboxGroup.class);
	}
	
	public String getEventTriggerId() {		
		return getId();
	}

	public FormResource getResource() {
		return RESOURCE;
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
	
	public String getInitScript() {
		return "new RiotCheckboxGroup('" + getId() + "');";
	}

	public boolean isCompositeElement() {
		return true;
	}
}
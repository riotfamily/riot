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
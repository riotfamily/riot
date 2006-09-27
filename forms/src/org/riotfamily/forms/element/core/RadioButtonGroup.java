package org.riotfamily.forms.element.core;

import java.io.PrintWriter;

import org.riotfamily.common.collection.FlatMap;
import org.riotfamily.forms.element.support.select.AbstractSingleSelectElement;
import org.riotfamily.forms.element.support.select.InputTagRenderer;
import org.riotfamily.forms.template.TemplateUtils;

/**
 * Single-select element that uses a group of radio-buttons to render 
 * the options. Internally a template is used in order to allow the 
 * customization of the layout.
 */
public class RadioButtonGroup extends AbstractSingleSelectElement {

	private String template;
	
	public RadioButtonGroup() {
		setOptionRenderer(new InputTagRenderer("radio"));
		template = TemplateUtils.getTemplatePath(this);
	}
	
	public void setTemplate(String template) {
		this.template = template;
	}

	public void renderInternal(PrintWriter writer) {
		FlatMap model = new FlatMap();
		model.put("element", this);
		model.put("options", getOptions());
		getFormContext().getTemplateRenderer().render(template, model, writer);
	}

}
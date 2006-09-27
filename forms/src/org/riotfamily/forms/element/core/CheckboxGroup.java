package org.riotfamily.forms.element.core;

import java.io.PrintWriter;

import org.riotfamily.common.collection.FlatMap;
import org.riotfamily.forms.element.DHTMLElement;
import org.riotfamily.forms.element.support.select.AbstractMultiSelectElement;
import org.riotfamily.forms.element.support.select.InputTagRenderer;
import org.riotfamily.forms.template.TemplateUtils;


/**
 * Multi-select element that uses a group of checkboxes to render the options.
 * Internally a template is used in order to allow the customization of
 * the layout.
 */
public class CheckboxGroup extends AbstractMultiSelectElement 
		implements DHTMLElement {

	private String template;

	public CheckboxGroup() {
		setOptionRenderer(new InputTagRenderer("checkbox"));
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
	
	public String getInitScript() {
		return TemplateUtils.getInitScript(this, CheckboxGroup.class);
	}
	
	public String getPrecondition() {
		return null;
	}

}
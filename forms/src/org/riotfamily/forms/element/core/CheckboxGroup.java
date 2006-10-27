/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
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
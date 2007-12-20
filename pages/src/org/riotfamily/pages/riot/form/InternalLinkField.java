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
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.riot.form;

import java.io.PrintWriter;

import org.riotfamily.common.markup.DocumentWriter;
import org.riotfamily.common.markup.Html;
import org.riotfamily.forms.DHTMLElement;
import org.riotfamily.forms.MessageUtils;
import org.riotfamily.forms.TemplateUtils;
import org.riotfamily.forms.element.TextField;
import org.riotfamily.forms.resource.FormResource;
import org.riotfamily.forms.resource.ResourceElement;
import org.riotfamily.forms.resource.Resources;
import org.riotfamily.forms.resource.ScriptResource;
import org.riotfamily.forms.resource.StylesheetResource;
import org.riotfamily.pages.riot.chooser.PageChooserController;

public class InternalLinkField extends TextField implements ResourceElement,
	DHTMLElement {

	private static final ScriptResource RESOURCE = new ScriptResource(
			"riot-js/window-callback.js", "WindowCallback",
			new FormResource[] {
				Resources.PROTOTYPE,
				new StylesheetResource("style/internal-link-field.css")
			}
	);

	private String chooserUrl = "/riot/pages/chooser";


	public InternalLinkField() {
		setStyleClass("text internal-link");
	}

	public String getChooserUrl() {
		return this.chooserUrl;
	}
	
	public String getChooserQueryString() {
		String pageId = (String) getForm().getAttribute("pageId");
		if (pageId == null) {
			return "";
		}
		return "?" + PageChooserController.PAGE_ID_PARAM + "=" + pageId;
	}

	public void setChooserUrl(String chooserUrl) {
		this.chooserUrl = chooserUrl;
	}

	public FormResource getResource() {
		return RESOURCE;
	}

	public String getInitScript() {
		return TemplateUtils.getInitScript(this);
	}

	public String getButtonId() {
		return getId() + "-button";
	}

	public void renderInternal(PrintWriter writer) {
		DocumentWriter doc = new DocumentWriter(writer);
		doc.start(Html.DIV).attribute(Html.COMMON_CLASS, "internal-link");
		doc.start(Html.DIV).attribute(Html.COMMON_CLASS, "input-wrapper");
		doc.body();

		super.renderInternal(writer);

		doc.end();
		String buttonLabel = MessageUtils.getMessage(this,
				"internalLinkField.buttonLabel", null, "Internal ...");

		doc.startEmpty(Html.INPUT)
				.attribute(Html.INPUT_TYPE, "button")
				.attribute(Html.INPUT_VALUE, buttonLabel)
				.attribute(Html.COMMON_ID, getButtonId())
				.attribute(Html.COMMON_CLASS, "button")
				.closeAll();
	}

}

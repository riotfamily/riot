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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.forms.element.suggest;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.markup.DocumentWriter;
import org.riotfamily.common.markup.Html;
import org.riotfamily.common.markup.TagWriter;
import org.riotfamily.forms.ContentElement;
import org.riotfamily.forms.DHTMLElement;
import org.riotfamily.forms.element.AbstractTextElement;
import org.riotfamily.forms.resource.FormResource;
import org.riotfamily.forms.resource.ResourceElement;
import org.riotfamily.forms.resource.Resources;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class AutocompleteTextField extends AbstractTextElement 
		implements ResourceElement, DHTMLElement, ContentElement {

	private AutocompleterModel model;

	public void setModel(AutocompleterModel model) {
		this.model = model;
	}

	public FormResource getResource() {
		return Resources.SCRIPTACULOUS_CONTROLS;
	}
	
	public void renderInternal(PrintWriter writer) {
		super.renderInternal(writer);
		TagWriter tag = new TagWriter(writer);
		tag.start(Html.DIV).attribute(Html.COMMON_ID, getChoicesDivId())
				.attribute(Html.COMMON_CLASS, "autocomplete")
				.attribute(Html.COMMON_STYLE, "display:none")
				.end();
	}
	
	private String getChoicesDivId() {
		return getId() + "-choices";
	}
	
	public String getInitScript() {
		StringBuffer sb = new StringBuffer();
		sb.append("new Ajax.Autocompleter(\"").append(getEventTriggerId()).append("\", \"")
			.append(getChoicesDivId()).append("\", \"")
			.append(getFormContext().getContentUrl(this)).append("\", {});");
		
		return sb.toString();
	}
		
	public void handleContentRequest(HttpServletRequest request, 
			HttpServletResponse response) throws IOException {

		String search = request.getParameter(getParamName());
		DocumentWriter doc = new DocumentWriter(response.getWriter());
		doc.start(Html.UL);
		for (String value : model.getSuggestions(search, this)) {
			doc.start(Html.LI).body(value).end();
		}
		doc.end();
	}
}

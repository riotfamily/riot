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
package org.riotfamily.forms.element;

import java.io.PrintWriter;

import org.riotfamily.common.markup.Html;
import org.riotfamily.common.markup.TagWriter;
import org.riotfamily.forms.DHTMLElement;
import org.riotfamily.forms.TemplateUtils;
import org.riotfamily.forms.resource.FormResource;
import org.riotfamily.forms.resource.ResourceElement;
import org.riotfamily.forms.resource.ScriptResource;


/**
 * A WYSIWYG richtext editor based on TinyMCE.
 */
public class TinyMCE extends AbstractTextElement
		implements ResourceElement, DHTMLElement {

	private int rows = 10;

	public TinyMCE() {
		setStyleClass("richtext");
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public void renderInternal(PrintWriter writer) {
		TagWriter tag = new TagWriter(writer);
		tag.start(Html.TEXTAREA)
			.attribute(Html.COMMON_CLASS, getStyleClass())
			.attribute(Html.COMMON_ID, getId())
			.attribute(Html.INPUT_NAME, getParamName())
			.attribute(Html.TEXTAREA_ROWS, rows)
			.body(getText()).end();
	}

	public FormResource getResource() {
		return new ScriptResource("tiny_mce/tiny_mce_src.js", "tinymce.WindowManager");
	}

	public String getLanguage() {
		return getFormContext().getLocale().getLanguage().toLowerCase();
	}

	public String getInitScript() {
		return TemplateUtils.getInitScript(this);
	}

}

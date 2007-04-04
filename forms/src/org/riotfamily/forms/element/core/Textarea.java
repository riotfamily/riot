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
package org.riotfamily.forms.element.core;

import java.io.PrintWriter;

import org.riotfamily.common.markup.Html;
import org.riotfamily.common.markup.TagWriter;
import org.riotfamily.forms.element.DHTMLElement;
import org.riotfamily.forms.element.support.AbstractTextElement;
import org.riotfamily.forms.resource.FormResource;
import org.riotfamily.forms.resource.ResourceElement;
import org.riotfamily.forms.resource.Resources;
import org.riotfamily.forms.resource.ScriptResource;


/**
 * A textarea widget.
 */
public class Textarea extends AbstractTextElement implements ResourceElement, 
		DHTMLElement {

	private static FormResource RESOURCE = new ScriptResource(
			"riot-js/textarea.js", "RiotTextArea", Resources.PROTOTYPE);
	
	private int rows = 5;

	private int cols = 80;

	private boolean autoResize;
	
	public void setCols(int cols) {
		this.cols = cols;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public void setAutoResize(boolean autoResize) {
		this.autoResize = autoResize;
	}

	public void renderInternal(PrintWriter writer) {
		TagWriter tag = new TagWriter(writer);
		tag.start(Html.TEXTAREA)
			.attribute(Html.COMMON_CLASS, getStyleClass())
			.attribute(Html.COMMON_ID, getId())
			.attribute(Html.INPUT_NAME, getParamName())
			.attribute(Html.TEXTAREA_ROWS, rows)
			.attribute(Html.TEXTAREA_COLS, cols);

		tag.body(getText()).end();
	}
	
	public FormResource getResource() {
		return RESOURCE;
	}
	
	public String getInitScript() {
		if (getMaxLength() != null || autoResize) {
			StringBuffer sb = new StringBuffer();
			sb.append("new RiotTextArea('").append(getId()).append("')");
			if (getMaxLength() != null) {
				sb.append(".setMaxLength(").append(getMaxLength()).append(')');
			}
			if (autoResize) {
				sb.append(".autoResize()");
			}
			sb.append(';');
			return sb.toString();
		}
		return null;
	}
	
}
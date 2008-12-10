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

import org.riotfamily.common.markup.DocumentWriter;
import org.riotfamily.forms.DHTMLElement;
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
			"riot-js/textarea.js", "RiotTextArea", Resources.RIOT_UTIL);
	
	private Integer rows = null;

	private Integer cols = null;

	public void setCols(Integer cols) {
		this.cols = cols;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}

	public void renderInternal(PrintWriter writer) {
		DocumentWriter doc = new DocumentWriter(writer);
		if (getMaxLength() == null && rows != null) {
			// If no init script is rendered we surround the textarea with a 
			// div so that we work around the IE 100% width bug via CSS:
			// http://fplanque.net/2003/Articles/iecsstextarea/
			doc.start("div").attribute("class", "textarea-wrapper");
		}
		doc.start("textarea")
			.attribute("id", getEventTriggerId())
			.attribute("class", getStyleClass())			
			.attribute("name", getParamName())
			.attribute("disabled", !isEnabled());
		
		if (rows != null) {
			doc.attribute("rows", rows.intValue());
		}
		if (cols != null) {
			doc.attribute("cols", cols.intValue());
		}
		
		doc.body(getText()).closeAll();
	}
	
	public FormResource getResource() {
		return RESOURCE;
	}
	
	public String getInitScript() {
		if (getMaxLength() != null || rows == null) {
			StringBuffer sb = new StringBuffer();
			sb.append("new RiotTextArea('").append(getEventTriggerId()).append("')");
			if (getMaxLength() != null) {
				sb.append(".setMaxLength(").append(getMaxLength()).append(')');
			}
			if (rows == null) {
				sb.append(".autoResize()");
			}
			sb.append(';');
			return sb.toString();
		}
		return null;
	}
	
}
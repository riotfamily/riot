/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.forms.element;

import java.io.PrintWriter;

import org.riotfamily.common.util.DocumentWriter;
import org.riotfamily.forms.DHTMLElement;
import org.riotfamily.forms.resource.FormResource;
import org.riotfamily.forms.resource.ResourceElement;
import org.riotfamily.forms.resource.Resources;
import org.riotfamily.forms.resource.ScriptResource;
import org.riotfamily.forms.ui.Dimension;


/**
 * A textarea widget.
 */
public class Textarea extends AbstractTextElement implements ResourceElement, 
		DHTMLElement {

	private static FormResource RESOURCE = new ScriptResource(
			"riot/textarea.js", "RiotTextArea", Resources.RIOT_UTIL);
	
	private Integer rows = null;

	private Integer cols = null;

	public void setCols(Integer cols) {
		this.cols = cols;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}

	@Override
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
	
	@Override
	public Dimension getDimension() {
		return getFormContext().getSizing().getTextareaSize(rows, cols, getText());
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
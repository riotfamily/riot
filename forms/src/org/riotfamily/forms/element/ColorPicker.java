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

import org.riotfamily.common.util.TagWriter;
import org.riotfamily.forms.DHTMLElement;
import org.riotfamily.forms.resource.FormResource;
import org.riotfamily.forms.resource.ResourceElement;
import org.riotfamily.forms.resource.Resources;
import org.riotfamily.forms.resource.ScriptResource;
import org.riotfamily.forms.resource.StylesheetResource;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class ColorPicker extends AbstractTextElement
		implements ResourceElement, DHTMLElement {

	public ColorPicker() {
		setStyleClass("text color-input");
		setMaxLength(6);
	}

	@Override
	public void renderInternal(PrintWriter writer) {
		super.renderInternal(writer);
		TagWriter tag = new TagWriter(writer);
		tag.startEmpty("button").attribute("id", getId() + "-swatch")
				.attribute("class", "color-swatch").end();
	}
	public FormResource getResource() {
		return new ScriptResource(
			"form/colorPicker/colorPicker.js", "Control.ColorPicker",
			Resources.SCRIPTACULOUS_SLIDER,
			Resources.SCRIPTACULOUS_DRAG_DROP,
			new ScriptResource("form/colorPicker/yahoo.color.js", "YAHOO.util.Color"),
			new StylesheetResource("form/colorPicker/colorPicker.css")
		);
	}

	public String getInitScript() {
		StringBuffer sb = new StringBuffer("new Control.ColorPicker('")
				.append(getEventTriggerId()).append("', {IMAGE_BASE: '")
				.append(getFormContext().getContextPath())
				.append(getFormContext().getResourcePath())
				.append("form/colorPicker/").append("', swatch: '")
				.append(getId() + "-swatch").append("'});");

		return sb.toString();
	}

}

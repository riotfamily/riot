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
import org.riotfamily.forms.resource.StylesheetResource;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class ColorPicker extends AbstractTextElement
		implements ResourceElement, DHTMLElement {

	public ColorPicker() {
		setStyleClass("text color-input");
		setMaxLength(new Integer(6));
	}

	public void renderInternal(PrintWriter writer) {
		super.renderInternal(writer);
		TagWriter tag = new TagWriter(writer);
		tag.start(Html.BUTTON).attribute(Html.COMMON_ID, getId() + "-swatch")
				.attribute(Html.COMMON_CLASS, "color-swatch");
	}
	public FormResource getResource() {
		return new ScriptResource(
			"form/colorPicker/colorPicker.js", "Control.ColorPicker",
			new FormResource[] {
				Resources.SCRIPTACULOUS_SLIDER,
				Resources.SCRIPTACULOUS_DRAG_DROP,
				new ScriptResource("form/colorPicker/yahoo.color.js", "YAHOO.util.Color"),
				new StylesheetResource("form/colorPicker/colorPicker.css")
			}
		);
	}

	public String getInitScript() {
		StringBuffer sb = new StringBuffer("new Control.ColorPicker('")
				.append(getId()).append("', {IMAGE_BASE: '")
				.append(getFormContext().getContextPath())
				.append(getFormContext().getResourcePath())
				.append("form/colorPicker/").append("', swatch: '")
				.append(getId() + "-swatch").append("'});");

		return sb.toString();
	}

}

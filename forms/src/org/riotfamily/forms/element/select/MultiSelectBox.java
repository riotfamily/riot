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
package org.riotfamily.forms.element.select;

import java.io.PrintWriter;
import java.util.List;

import org.riotfamily.common.markup.TagWriter;


/**
 * A select-box widget that allows multiple selections.
 */
public class MultiSelectBox extends AbstractMultiSelectElement {

	private int maxSize = 7;

	public MultiSelectBox() {
		setOptionRenderer(new OptionTagRenderer());
	}

	public int getMaxSize() {
		return this.maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	protected void renderInternal(PrintWriter writer) {
		TagWriter selectTag = new TagWriter(writer);

		List<OptionItem> options = getOptionItems();
		selectTag.start("select");
		selectTag.attribute("id", getEventTriggerId());
		selectTag.attribute("name", getParamName());
		selectTag.attribute("size", Math.min(options.size(), maxSize));
		selectTag.attribute("multiple", true);
		selectTag.body();
		for (OptionItem item : options) {
			item.render();
		}
		selectTag.end();
	}

}

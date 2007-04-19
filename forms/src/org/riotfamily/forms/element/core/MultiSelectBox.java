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
import java.util.Iterator;
import java.util.List;

import org.riotfamily.common.markup.Html;
import org.riotfamily.common.markup.TagWriter;
import org.riotfamily.forms.element.support.select.AbstractMultiSelectElement;
import org.riotfamily.forms.element.support.select.Option;
import org.riotfamily.forms.element.support.select.OptionTagRenderer;


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

	public void renderInternal(PrintWriter writer) {
		TagWriter selectTag = new TagWriter(writer);

		List options = getOptions();
		selectTag.start(Html.SELECT);
		selectTag.attribute(Html.INPUT_NAME, getParamName());
		selectTag.attribute(Html.COMMON_ID, getId());
		selectTag.attribute(Html.SELECT_SIZE, Math.min(options.size(), maxSize));
		selectTag.attribute(Html.SELECT_MULTIPLE, true);
		selectTag.body();

		Iterator it = options.iterator();
		while (it.hasNext()) {
			((Option) it.next()).render();
		}

		selectTag.end();
	}

}

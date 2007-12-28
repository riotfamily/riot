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
import java.util.Iterator;
import java.util.List;

import org.riotfamily.common.markup.Html;
import org.riotfamily.common.markup.TagWriter;


/**
 * A select-box widget.
 */
public class SelectBox extends AbstractSingleSelectElement {

	private static final String DEFAULT_CHOOSE_LABEL_KEY = 
			"label.selectBox.choose";
	
	private String chooseLabelKey;

	private String chooseLabel;
	
	public SelectBox() {
		setOptionRenderer(new OptionTagRenderer());
	}

	/**
	 * @deprecated Use @link #setChooseLabel(String) or 
	 * @link #setChooseLabelKey(String) instead.
	 */
	public void setShowChooseOption(boolean show) {
		if (show) {
			chooseLabelKey = DEFAULT_CHOOSE_LABEL_KEY;
		}
		else {
			chooseLabelKey = null;
			chooseLabel = null;
		}
	}

	public void setChooseLabel(String chooseLabel) {
		this.chooseLabel = chooseLabel;
	}

	public void setChooseLabelKey(String chooseLabelKey) {
		this.chooseLabelKey = chooseLabelKey;
	}

	protected List createOptions() {
		List options = super.createOptions();
		if (chooseLabelKey != null) {
			chooseLabel = getFormContext().getMessageResolver()
					.getMessage(chooseLabelKey);
		}
		if (chooseLabel != null) {
			Option chooseOption = new Option(null, null, chooseLabel, this);
			options.add(0, chooseOption);
		}
		else if (!isRequired()) {
			Option emptyOption = new Option(null, null, "", this);
			options.add(0, emptyOption);
		}
		return options;
	}
	
	public void renderInternal(PrintWriter writer) {
		TagWriter selectTag = new TagWriter(writer);

		selectTag.start(Html.SELECT);
		selectTag.attribute(Html.INPUT_NAME, getParamName());
		selectTag.attribute(Html.COMMON_ID, getId());
		selectTag.attribute(Html.SELECT_SIZE, 1);
		selectTag.attribute(Html.INPUT_DISABLED, !isEnabled());
		selectTag.body();

		Iterator it = getOptions().iterator();
		while (it.hasNext()) {
			((Option) it.next()).render();
		}

		selectTag.end();
	}

}

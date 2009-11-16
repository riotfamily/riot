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
package org.riotfamily.forms.element.select;

import java.io.PrintWriter;
import java.util.List;

import org.riotfamily.common.util.TagWriter;
import org.riotfamily.forms.ui.Dimension;


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

	@Override
	protected void renderInternal(PrintWriter writer) {
		TagWriter selectTag = new TagWriter(writer);

		List<OptionItem> options = getOptionItems();
		selectTag.start("select");
		selectTag.attribute("id", getEventTriggerId());
		selectTag.attribute("name", getParamName());
		selectTag.attribute("size", getSize());
		selectTag.attribute("multiple", true);
		selectTag.body();
		for (OptionItem item : options) {
			item.render();
		}
		selectTag.end();
	}
	
	private int getSize() {
		return Math.min(getOptionItems().size(), maxSize);
	}

	public Dimension getDimension() {
		return getFormContext().getSizing().getSelectBoxSize(getSize());
	}

}

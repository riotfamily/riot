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

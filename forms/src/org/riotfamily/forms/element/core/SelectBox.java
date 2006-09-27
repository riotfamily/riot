package org.riotfamily.forms.element.core;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import org.riotfamily.common.markup.Html;
import org.riotfamily.common.markup.TagWriter;
import org.riotfamily.forms.element.support.select.AbstractSingleSelectElement;
import org.riotfamily.forms.element.support.select.Option;
import org.riotfamily.forms.element.support.select.OptionTagRenderer;


/**
 * A select-box widget.
 */
public class SelectBox extends AbstractSingleSelectElement {

	private static final String DEFAULT_CHOOSE_LABEL_KEY = 
			"label.selectBox.choose";
	
	private int size = 1;

	private boolean multiple;

	private String chooseLabelKey;

	private String chooseLabel;
	
	public SelectBox() {
		setOptionRenderer(new OptionTagRenderer());
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public boolean isMultiple() {
		return multiple;
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

	public void setMultiple(boolean multiSelect) {
		this.multiple = multiSelect;
	}

	protected List createOptions() {
		List options = super.createOptions();
		if (chooseLabelKey != null) {
			chooseLabel = getFormContext().getMessageResolver()
					.getMessage(chooseLabelKey);
		}
		if (chooseLabel != null) {
			Option chooseOption = new Option(null, chooseLabel, this);
			options.add(0, chooseOption);
		}
		return options;
	}
	
	public void renderInternal(PrintWriter writer) {
		TagWriter selectTag = new TagWriter(writer);

		selectTag.start(Html.SELECT);
		selectTag.attribute(Html.INPUT_NAME, getParamName());
		selectTag.attribute(Html.COMMON_ID, getId());
		selectTag.attribute(Html.SELECT_SIZE, size);
		selectTag.attribute(Html.SELECT_MULTIPLE, isMultiple());
		selectTag.body();

		Iterator it = getOptions().iterator();
		while (it.hasNext()) {
			((Option) it.next()).render();
		}

		selectTag.end();
	}

}

package org.riotfamily.forms.element.select;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;

import org.riotfamily.common.markup.TagWriter;
import org.riotfamily.forms.MessageUtils;


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
	
	protected List<OptionItem> createOptionItems(Collection<?> values) {
		List<OptionItem> optionItems = super.createOptionItems(values);
		if (chooseLabelKey != null) {
			chooseLabel = MessageUtils.getMessage(this, chooseLabelKey);
		}
		if (chooseLabel != null) {
			OptionItem chooseOption = new OptionItem(null, null, chooseLabel, null, this);
			optionItems.add(0, chooseOption);
		}
		else if (!isRequired()) {
			OptionItem emptyOption = new OptionItem(null, null, "", null, this);
			optionItems.add(0, emptyOption);
		}
		return optionItems;
	}
	
	public Object getValue() {
		List<OptionItem> optionItems = getOptionItems();
		if (optionItems.size() == 1 && isRequired()) {
			return optionItems.get(0).getValue();
		}
		return super.getValue();
	}
			
	protected void renderInternal(PrintWriter writer) {
		List<OptionItem> optionItems = getOptionItems();
		TagWriter selectTag = new TagWriter(writer)
				.start("select")
				.attribute("id", getEventTriggerId())
				.attribute("class", getStyleClass())
				.attribute("name", getParamName())			
				.attribute("size", 1)
				.attribute("disabled", !isEnabled())
				.body();
		
		for (OptionItem item : optionItems) {
			item.render();
		}
		selectTag.end();
		
	}

}

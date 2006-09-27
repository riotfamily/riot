package org.riotfamily.forms.element.support.select;

import java.io.PrintWriter;

import org.riotfamily.common.markup.Html;
import org.riotfamily.common.markup.TagWriter;


/**
 *
 */
public class InputTagRenderer implements OptionRenderer {
	
	private String type;
	
	public InputTagRenderer(String type) {
		this.type = type;
	}
	
	public void renderOption(Option option, PrintWriter writer) {
		TagWriter optionTag = new TagWriter(writer);
		optionTag.startEmpty(Html.INPUT);
		optionTag.attribute(Html.INPUT_TYPE,type);
		optionTag.attribute(Html.INPUT_NAME, option.getParent().getParamName());
		optionTag.attribute(Html.INPUT_VALUE, option.getIndex());
		optionTag.attribute(Html.INPUT_CHECKED, option.isSelected());
		optionTag.attribute(Html.COMMON_ID, option.getId());
		optionTag.end();
	}	

}

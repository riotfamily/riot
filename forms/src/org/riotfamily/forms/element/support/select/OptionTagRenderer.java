package org.riotfamily.forms.element.support.select;

import java.io.PrintWriter;

import org.riotfamily.common.markup.Html;
import org.riotfamily.common.markup.TagWriter;


/**
 *
 */
public class OptionTagRenderer implements OptionRenderer {

	public void renderOption(Option option, PrintWriter writer) {
		TagWriter optionTag = new TagWriter(writer);
		optionTag.start(Html.OPTION);
		optionTag.attribute(Html.COMMON_ID, option.getId());
		optionTag.attribute(Html.INPUT_VALUE, option.getIndex());
		optionTag.attribute(Html.OPTION_SELECTED, option.isSelected());
		optionTag.body(option.getLabel());
		optionTag.end();
	}

}
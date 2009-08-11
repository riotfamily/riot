package org.riotfamily.forms.element.select;

import java.io.PrintWriter;

import org.riotfamily.common.markup.TagWriter;


/**
 *
 */
public class InputTagRenderer implements OptionRenderer {
	
	private String type;
	
	public InputTagRenderer(String type) {
		this.type = type;
	}
	
	public void renderOption(OptionItem option, PrintWriter writer, boolean enabled) {
		TagWriter optionTag = new TagWriter(writer);
		optionTag.startEmpty("input");
		optionTag.attribute("type", type);
		optionTag.attribute("name", option.getParent().getParamName());
		optionTag.attribute("value", option.getIndex());
		optionTag.attribute("checked", option.isSelected());
		optionTag.attribute("disabled", !enabled);
		optionTag.attribute("id", option.getId());
		optionTag.attribute("class", type);
		optionTag.end();
	}	

}

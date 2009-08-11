package org.riotfamily.forms.element.select;

import java.io.PrintWriter;

import org.riotfamily.common.markup.TagWriter;
import org.riotfamily.common.util.FormatUtils;


/**
 *
 */
public class OptionTagRenderer implements OptionRenderer {

	public void renderOption(OptionItem option, PrintWriter writer, boolean enabled) {
		new TagWriter(writer)
				.start("option")
				.attribute("id", option.getId())
				.attribute("class", option.getStyleClass())
				.attribute("value", option.getIndex())
				.attribute("selected", option.isSelected())
				.body(FormatUtils.stripTags(option.getLabel()))
				.end();
	}

}
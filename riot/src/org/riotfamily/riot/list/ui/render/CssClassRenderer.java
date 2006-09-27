package org.riotfamily.riot.list.ui.render;

import java.io.PrintWriter;

import org.riotfamily.common.util.FormatUtils;

public class CssClassRenderer extends ObjectRenderer {

	protected void renderValue(RenderContext context, PrintWriter writer, 
			String value) {
		
		writer.print("<div class=\"css-cell ");
		writer.print(FormatUtils.toCssClass(value));
		writer.print("\"></div>");
	}

}

package org.riotfamily.riot.list.ui.render;

import java.io.PrintWriter;

public class ImageRenderer extends ObjectRenderer {

	protected void renderValue(RenderContext context, PrintWriter writer, 
			String value) {
		
		if (value != null) {
			writer.print("<img src=\"");
			writer.print(context.getContextPath());
			writer.print(value);
			writer.print("\" />");
		}
	}

}

package org.riotfamily.common.ui;

import java.io.PrintWriter;

public class ImageRenderer extends StringRenderer {

	protected void renderString(String string, RenderContext context, 
			PrintWriter writer) {
		
		if (string != null) {
			writer.print("<img src=\"");
			writer.print(context.getContextPath());
			writer.print(string);
			writer.print("\" />");
		}
	}

}

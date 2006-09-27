package org.riotfamily.riot.list.ui.render;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateRenderer implements CellRenderer {

	private static final String SHORT = "short";
	
	private static final String MEDIUM = "medium";
	
	private static final String LONG = "long";
	
	
	private int style = DateFormat.MEDIUM;
	
	public void setStyle(String style) {
		if (SHORT.equals(style)) {
			this.style = DateFormat.SHORT;
		}
		else if (MEDIUM.equals(style)) {
			this.style = DateFormat.MEDIUM;
		} 
		else if (LONG.equals(style)) {
			this.style = DateFormat.LONG;
		}
		else {
			throw new IllegalArgumentException("Invalid date style: " + style);
		}
	}
	
	public void render(RenderContext context, PrintWriter writer) {
		Date date = (Date) context.getValue();
		if (date != null) {
			DateFormat format = SimpleDateFormat.getDateInstance(
					style, context.getLocale());
			
			writer.print(format.format(date));
		}
	}
}

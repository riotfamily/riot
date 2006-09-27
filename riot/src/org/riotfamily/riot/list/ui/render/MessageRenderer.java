package org.riotfamily.riot.list.ui.render;

import java.io.PrintWriter;

public class MessageRenderer implements CellRenderer {
	
	public void render(RenderContext context, PrintWriter writer) {
		Object value = context.getValue();
		if (value != null) {
			StringBuffer key = new StringBuffer();
			key.append(context.getListConfig().getId());
			key.append('.');
			key.append(context.getProperty());
			
			if (value instanceof String) {
				key.append('.').append(value);
			}
		
			writer.print(context.getMessageResolver().getMessage(key.toString(), 
					new Object[] { value }, value.toString()));
		}
	}

	

}

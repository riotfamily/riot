package org.riotfamily.dbmsgsrc.riot;

import java.io.PrintWriter;

import org.riotfamily.common.ui.RenderContext;
import org.riotfamily.common.ui.StringRenderer;
import org.riotfamily.dbmsgsrc.model.Message;
import org.riotfamily.dbmsgsrc.model.MessageBundleEntry;

public class LocalMessageCellRenderer extends StringRenderer {
	
	public void render(Object obj, RenderContext context, PrintWriter writer) {
		Message message = (Message) obj;
		if (!MessageBundleEntry.C_LOCALE.equals(message.getLocale())) {
			super.render(message.getText(), context, writer);			
		}		
	}

}

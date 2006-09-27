package org.riotfamily.riot.list.ui.render;

import java.io.PrintWriter;

import org.riotfamily.common.markup.DocumentWriter;
import org.riotfamily.common.markup.Html;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.riot.list.command.Command;
import org.riotfamily.riot.security.AccessController;

/**
 * CellRenderer that renders a command.
 */
public class CommandRenderer implements CellRenderer {
	
	private final String COMMAND_MESSAGE_PREFIX = "command.";

	public boolean renderDisabled = true;
	
	public boolean renderText = false;

	public void setRenderDisabled(boolean renderDisabled) {
		this.renderDisabled = renderDisabled;
	}

	public void setRenderText(boolean renderText) {
		this.renderText = renderText;
	}

	public void render(RenderContext context, PrintWriter writer) {
		Command command = context.getCommand();
		if (command != null) {
			boolean enabled = command.isEnabled(context) 
				&& AccessController.isGranted(command.getAction(context), 
						context.getItem(), context.getListDefinition());
			
			if (enabled || renderDisabled) {
				DocumentWriter doc = new DocumentWriter(writer);
				doc.start(enabled ? Html.A : Html.DIV);
				if (enabled) {
					doc.attribute(Html.A_HREF, "#");
				}
				StringBuffer classAttr = new StringBuffer();
				if (enabled) {
					classAttr.append("command-");
					classAttr.append(command.getId());
					classAttr.append(' ');
				}
				
				String[] classes = new String[] {
						"action", 
						command.getAction(context),
						enabled ? null : "disabled"
				};
				
				classAttr.append(FormatUtils.combine(classes));
				doc.attribute(Html.COMMON_CLASS, classAttr.toString());
				String commandName = context.getMessageResolver().getMessage(COMMAND_MESSAGE_PREFIX + command.getId(), null,FormatUtils.camelToTitleCase(command.getId()));
				doc.attribute(Html.TITLE,commandName);
				if (renderText) {
					doc.start(Html.SPAN)
						.attribute(Html.COMMON_CLASS)
						.body(commandName);
				}
				doc.closeAll();
			}
		}
	}

}

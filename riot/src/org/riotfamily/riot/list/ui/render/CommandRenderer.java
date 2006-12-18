/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
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
					classAttr.append("enabled-command command-");
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
				if (renderText) {
					doc.start(Html.SPAN)
						.attribute(Html.COMMON_CLASS)
						.body(commandName, false);
				}
				else {
					doc.attribute(Html.TITLE, commandName);
				}
				doc.closeAll();
			}
		}
	}

}

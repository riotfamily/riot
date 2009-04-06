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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.core.screen.list.command;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.riotfamily.common.web.mapping.HandlerUrlUtils;
import org.riotfamily.core.screen.list.command.result.CommandResult;
import org.riotfamily.core.screen.list.command.result.DialogResult;
import org.riotfamily.forms.Form;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public abstract class DialogCommand extends AbstractCommand {

	public CommandResult execute(CommandContext context, Selection selection) {
		Form form = createForm(selection);
		
		form.setAttribute("commandId", context.getCommandId());
		form.setAttribute("selection", selection);
		form.setAttribute("listStateKey", context.getListKey());
		
		String formSessionAttribute = "foo"; //FIXME Generate a distinct key
		context.getRequest().getSession().setAttribute(formSessionAttribute, form);
		String formUrl = HandlerUrlUtils.getContextRelativeUrl(context.getRequest(), 
				"commandDialogController", formSessionAttribute);

		form.setFormContext(context.createFormContext(formUrl));
		form.init();
		
		StringWriter sw = new StringWriter();
		form.render(new PrintWriter(sw));

		return new DialogResult().setContent(sw.toString()).setCloseButton(true);
	}
		
	public abstract Form createForm(Selection selection);
	
	public CommandResult handleInput(CommandContext context, Selection selection,
			Object input, String button) {
		
		return null;
	}
	
}

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
package org.riotfamily.riot.list.command.dialog;

import java.util.Map;

import org.riotfamily.common.util.Generics;
import org.riotfamily.forms.Form;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.core.AbstractCommand;
import org.riotfamily.riot.list.command.result.GotoUrlResult;
import org.riotfamily.riot.list.ui.ListSession;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public abstract class DialogCommand extends AbstractCommand {

	public final CommandResult execute(CommandContext context) {
		Form form = createForm(context.getBean());
		form.setAttribute("listUrl", context.getListUrl());
		context.getRequest().getSession().setAttribute(
				getFormSessionAttribute(), form);
		
		Map<String, String> attributes = Generics.newHashMap();
		attributes.put("commandId", getId());
		attributes.put("listSessionKey", context.getListSessionKey());

		return new GotoUrlResult(context, getRuntime().getUrl(
				"commandDialogController", attributes));
	}
	
	public String getFormSessionAttribute() {
		return getClass().getName() + ".form";
	}
	
	public abstract Form createForm(Object bean);
	
	public abstract ModelAndView handleInput(Object input, Object bean, ListSession listSession);
	
}

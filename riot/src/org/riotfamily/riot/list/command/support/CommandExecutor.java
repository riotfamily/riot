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
package org.riotfamily.riot.list.command.support;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.forms.Form;
import org.riotfamily.riot.editor.EditorDefinitionUtils;
import org.riotfamily.riot.editor.FormDefinition;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.form.command.FormCommandContext;
import org.riotfamily.riot.list.ListConfig;
import org.riotfamily.riot.list.ListRepository;
import org.riotfamily.riot.list.command.Command;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.ui.ListContext;
import org.springframework.util.Assert;
import org.springframework.web.util.HtmlUtils;

public class CommandExecutor {

	public void executeCommand(ListRepository repository, 
			ListContext listContext) {
		
		Command command = listContext.getCommand();
		
		CommandExecutionContext context = 
				new CommandExecutionContext(listContext);
		
		HttpServletRequest request = listContext.getRequest();
		HttpServletResponse response = listContext.getResponse();
		
		executeCommand(command, context, request, response);
	}
	
	public void executeCommand(ListRepository listRepository, 
			FormDefinition formDefinition, Form form, Object item, 
			HttpServletRequest request, HttpServletResponse response,
			Map model) {
		
		ListDefinition parentList = EditorDefinitionUtils
				.getParentListDefinition(formDefinition);
		
		ListConfig listConfig = listRepository.getListConfig(
				parentList.getListId());
		
		FormCommandContext context = new FormCommandContext(
				formDefinition, form, listConfig, item, request, response);
		
		String commandId = request.getParameter("command");
		Command command = listRepository.getCommand(commandId);
		Assert.notNull(command, "No such command: " + commandId);
		model.put("command", commandId);
		
		String confirmation = command.getConfirmationMessage(context);
		if (confirmation != null && !context.isConfirmed()) {
			model.put("confirmCommand", confirmation);
		}
		else {
			CommandResult action = command.execute(context);
			if (action != null) {
				String result = action.getJavaScriptCode(request, response);
				model.put("commandResult", result);
			}
		}
	}
	
	public void executeCommand(ListRepository listRepository, 
			FormCommandContext context, HttpServletRequest request, 
			HttpServletResponse response, Map model) {		
		
		String commandId = request.getParameter("command");
		Command command = listRepository.getCommand(commandId);		
		executeCommand(command, context, request, response);		
	}
	
	private void executeCommand(Command command, 
			CommandContext context, HttpServletRequest request, 
			HttpServletResponse response) {

		try {
			response.setContentType("text/xml;charset=UTF-8");
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Expires", "0");
			response.setHeader("Cache-Control", "no-store");
			
			PrintWriter out = response.getWriter();
			out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			out.println("<command-response>");
			String confirmation = command.getConfirmationMessage(context);
			if (confirmation != null && !context.isConfirmed()) {
				String url = HtmlUtils.htmlEscape(response.encodeURL(
						request.getRequestURL()
						.append('?')
						.append(request.getQueryString())
						.append("&confirmed=true").toString()));
				
				out.print("<confirm url=\"" + url + "\">" +
						FormatUtils.xmlEscape(confirmation) + "</confirm>");
			}
			else {
				CommandResult action = command.execute(context);
				if (action != null) {
					out.print("<eval>" 
							+ action.getJavaScriptCode(request, response)
							+ "</eval>");
				}
			}
			out.println();
			out.print("</command-response>");
		}
		catch (IOException e) {
		}
	}

}

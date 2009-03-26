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
package org.riotfamily.core.screen.list.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.util.Generics;
import org.riotfamily.core.command.Command;
import org.riotfamily.core.command.CommandContext;
import org.riotfamily.core.command.CommandInfo;
import org.riotfamily.core.command.CommandResult;
import org.riotfamily.core.command.Selection;
import org.riotfamily.core.command.dialog.DialogCommand;
import org.riotfamily.core.dao.RiotDao;
import org.riotfamily.core.screen.ScreenContext;
import org.riotfamily.core.screen.list.dto.CommandButton;
import org.riotfamily.core.screen.list.dto.ListItem;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.FormContext;
import org.springframework.transaction.TransactionStatus;

public class CommandContextHandler extends ListServiceHandler
		implements CommandContext {

	private String commandId;
	
	public CommandContextHandler(ListService service, String key,
			HttpServletRequest request) {
		
		super(service, key, request);
	}
	
	public CommandContextHandler(ListServiceHandler handler) {
		super(handler);
	}

	public List<CommandButton> createButtons(boolean formCommandsOnly) {
		ArrayList<CommandButton> result = Generics.newArrayList();
		Map<String, Command> commands = screen.getCommandMap();
		if (commands != null) {
			for (Map.Entry<String, ? extends Command> entry : commands.entrySet()) {
				commandId = entry.getKey();
				CommandInfo info = entry.getValue().getInfo(this);
				if (info != null && !formCommandsOnly || info.isShowOnForm()) {
					result.add(new CommandButton(commandId, info));
				}
			}
		}
		return result;
	}
	
	public List<String> getEnabledCommands(List<ListItem> items) {
		List<String> result = Generics.newArrayList();
		if (screen.getCommandMap() != null) {
			Selection selection = createSelection(items);
			for (Map.Entry<String, Command> entry : screen.getCommandMap().entrySet()) {
				commandId = entry.getKey();
				if (entry.getValue().isEnabled(this, selection)) {
					result.add(commandId);
				}
			}
		}
		return result;
	}
	
	public CommandResult execCommand(String commandId, List<ListItem> items) {
		this.commandId = commandId;
		CommandResult result = null;
		Command command = screen.getCommandMap().get(commandId);
		TransactionStatus status = beginTransaction();
		try {
			Selection selection = createSelection(items);
			result = command.execute(this, selection);
		}
		catch (RuntimeException e) {
			rollback(status);
			throw e;
		}
		commit(status);
		return result; 
	}
	
	private Selection createSelection(List<ListItem> items) {
		List<String> objectIds = Generics.newArrayList();
		if (items != null) {
			for (ListItem item : items) {
				objectIds.add(item.getObjectId());
			}
		}
		return new Selection(dao, objectIds);
	}

	public CommandResult handleDialogInput(Form form) {
		CommandResult result = null;
		TransactionStatus status = beginTransaction();
		try {
			Object input = form.populateBackingObject();
			List<String> objectIds = form.getAttribute("objectIds");
			Selection selection = new Selection(dao, objectIds);
			commandId = form.getAttribute("commandId");
			DialogCommand command = (DialogCommand) screen.getCommandMap().get(commandId);
			result = command.handleInput(this, selection, input, 
					form.getClickedButton());
		}
		catch (Exception e) {
			rollback(status);
			throw new RuntimeException(e); //REVISIT
		}
		commit(status);
		return result;
	}
	
	
	// -----------------------------------------------------------------------

	public String getCommandId() {
		return commandId;
	}
	
	public ScreenContext createItemContext(Object item) {
		return screenContext.createItemContext(item);
	}

	public ScreenContext createNewItemContext(Object parentTreeItem) {
		return screenContext.createNewItemContext(parentTreeItem);
	}
	
	public Object getParent() {
		return screenContext.getParent();
	}
	
	public FormContext createFormContext(String formUrl) {
		return service.getFormContextFactory().createFormContext(
				messageResolver, request.getContextPath(), formUrl);
	}
		
	public ListState getListState() {
		return state;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public RiotDao getDao() {
		return dao;
	}
	
}
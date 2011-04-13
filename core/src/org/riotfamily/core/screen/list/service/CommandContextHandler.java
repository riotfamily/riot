/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.core.screen.list.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.common.util.Generics;
import org.riotfamily.core.dao.ListParams;
import org.riotfamily.core.screen.ListScreen;
import org.riotfamily.core.screen.ScreenContext;
import org.riotfamily.core.screen.list.command.Command;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandInfo;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.SelectionItem;
import org.riotfamily.core.screen.list.command.impl.dialog.DialogCommand;
import org.riotfamily.core.screen.list.dto.CommandButton;
import org.riotfamily.core.screen.list.dto.ListItem;
import org.riotfamily.core.security.AccessController;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.FormContext;
import org.springframework.transaction.TransactionStatus;

/**
 * List service handler that handles command related tasks.
 * @author Felix Gnass [fgnass at neteye dot de]
 */
class CommandContextHandler extends ListServiceHandler
		implements CommandContext {

	private String commandId;
	
	private int itemsTotal = -1;
	
	CommandContextHandler(ListService service, String key,
			HttpServletRequest request) {
		
		super(service, key, request);
	}
	
	public List<CommandButton> createButtons() {
		ArrayList<CommandButton> result = Generics.newArrayList();
		Map<String, Command> commands = getCommands();
		if (commands != null) {
			for (Map.Entry<String, ? extends Command> entry : commands.entrySet()) {
				commandId = entry.getKey();
				CommandInfo info = entry.getValue().getInfo(this);
				if (info != null) {
					result.add(new CommandButton(commandId, info));
				}
			}
		}
		return result;
	}
	
	public List<CommandButton> createFormButtons(ListItem item) {
		ArrayList<CommandButton> result = Generics.newArrayList();
		List<ListItem> items = item != null ? Collections.singletonList(item) : null;
		Selection selection = new Selection(dao, items);
		Map<String, Command> commands = getCommands();
		if (commands != null) {
			for (Map.Entry<String, ? extends Command> entry : commands.entrySet()) {
				commandId = entry.getKey();
				Command command = entry.getValue();
				CommandInfo info = command.getInfo(this);
				if (info != null && info.isShowOnForm()) {
					boolean enabled = command.isEnabled(this, selection);
					result.add(new CommandButton(commandId, info, enabled));
				}
			}
		}
		return result;
	}
	
	protected Map<String, Command> getCommands() {
		return screen.getCommandMap();
	}
	
	public List<String> getEnabledCommands(List<ListItem> items) {
		List<String> result = Generics.newArrayList();
		if (getCommands() != null) {
			Selection selection = new Selection(dao, items);
			for (Map.Entry<String, Command> entry : getCommands().entrySet()) {
				commandId = entry.getKey();
				Command command = entry.getValue();
				if (command.isEnabled(this, selection) 
						&& isGranted(command, selection)) {
					
					result.add(commandId);
				}
			}
		}
		return result;
	}
	
	private boolean isGranted(Command command, Selection selection) {
		CommandInfo info = command.getInfo(this);
		if (info == null) {
			return false;
		}
		String action = info.getAction();
		if (action != null) {
			if (selection.isEmpty()) {
				return AccessController.isGranted(action, null, screenContext);
			}
			for (SelectionItem item : selection) {
				if (!AccessController.isGranted(action, item.getObject(), screenContext)) {
					return false;
				}
			}
		}
		return true;
	}

	public CommandResult execCommand(String commandId, List<ListItem> items) throws Exception {
		this.commandId = commandId;
		CommandResult result = null;
		Command command = getCommands().get(commandId);
		TransactionStatus status = beginTransaction();
		try {
			Selection selection = new Selection(dao, items);
			if (command.isEnabled(this, selection)) {
				String action = command.getInfo(this).getAction();
				if (action != null) {
					for (SelectionItem item : selection) {
						AccessController.assertIsGranted(action, item.getObject(), screenContext);
					}
				}
				result = command.execute(this, selection);
			}
		}
		catch (Exception e) {
			rollback(status);
			throw e;
		}
		commit(status);
		return result; 
	}
	
	public CommandResult handleDialogInput(Form form) {
		CommandResult result = null;
		TransactionStatus status = beginTransaction();
		try {
			Object input = form.populateBackingObject();
			Selection selection = form.getAttribute("selection");
			if (selection != null) {
				selection.resetObjects();
			}
			commandId = form.getAttribute("commandId");
			DialogCommand command = (DialogCommand) getCommands().get(commandId);
			result = command.handleInput(this, selection, input, 
					form.getClickedButton());
		}
		catch (Exception e) {
			rollback(status);
			throw new RuntimeException(e); //REVISIT Throw a more specialized exception?
		}
		commit(status);
		return result;
	}
	
	
	// -----------------------------------------------------------------------
	// Implementation of the CommandContext interface
	// -----------------------------------------------------------------------
	
	public String getCommandId() {
		return commandId;
	}
	
	public ScreenContext getScreenContext() {
		return screenContext;
	}
	
	public Object getParent() {
		return screenContext.getParent();
	}
	
	public FormContext createFormContext(String formUrl) {
		return service.getFormContextFactory().createFormContext(
				messageResolver, request.getContextPath(), formUrl);
	}
		
	public String getListKey() {
		return state.getKey();
	}

	public ListParams getParams() {
		return state.getParams();
	}
	
	public int getItemsTotal() {
		if (itemsTotal == -1) {
			itemsTotal = dao.getListSize(getParent(), getParams());
		}
		if (itemsTotal == -1) {
			itemsTotal = dao.list(getParent(), getParams()).size();
		}
		return itemsTotal;
	}
	
	public HttpServletRequest getRequest() {
		return request;
	}

	public ListScreen getScreen() {
		return screen;
	}
	
	public MessageResolver getMessageResolver() {
		return messageResolver;
	}
	
	public String getResourcePath() {
		return request.getContextPath() + service.getResourcePath();
	}
	
}
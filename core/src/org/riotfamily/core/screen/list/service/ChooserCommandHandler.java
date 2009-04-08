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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.util.Generics;
import org.riotfamily.core.screen.ListScreen;
import org.riotfamily.core.screen.ScreenContext;
import org.riotfamily.core.screen.ScreenUtils;
import org.riotfamily.core.screen.list.command.Command;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.impl.support.AbstractCommand;
import org.riotfamily.core.screen.list.command.result.GotoUrlResult;
import org.riotfamily.core.screen.list.command.result.ScriptResult;

/**
 * List service handler that handles object-chooser related tasks. 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class ChooserCommandHandler extends CommandContextHandler {

	private Map<String, Command> commands;
	
	private ListScreen nextList;
	
	public ChooserCommandHandler(ListService service, String key,
			HttpServletRequest request) {
		
		super(service, key, request);
	}

	protected Map<String, Command> getCommands() {
		if (commands == null) {
			if (chooserTarget != null) {
				commands = Generics.newHashMap();
				if (chooserTarget != screen) {
					nextList = chooserTarget;
					ListScreen parent = ScreenUtils.getParentListScreen(nextList); 
					while (parent != screen && parent != null) {
						nextList = parent;
						parent = ScreenUtils.getParentListScreen(nextList);
					}
					commands.put("descend", new DescendCommand());
				}
				else {
					if (dao.getEntityClass().isAssignableFrom(
							chooserTarget.getDao().getEntityClass())) {
			
						commands.put("choose", new ChooseCommand());
					}
				}
			}
			else {
				commands = screen.getCommandMap();
			}
		}
		return commands;
	}
	
	private class DescendCommand extends AbstractCommand {

		@Override
		public boolean isEnabled(CommandContext context, Selection selection) {
			return selection.size() == 1;
		}
		
		public CommandResult execute(CommandContext context, Selection selection) {
			ScreenContext nextContext = new ScreenContext(nextList, 
					selection.getSingleItem().getObject(), null, false, screenContext);
			
			return new GotoUrlResult(context.getRequest(), nextContext.getUrl());
		}
	}
	
	private class ChooseCommand extends AbstractCommand {

		@Override
		public boolean isEnabled(CommandContext context, Selection selection) {
			return selection.size() == 1;
		}
		
		public CommandResult execute(CommandContext context, Selection selection) {
			return new ScriptResult("parent.riot.chooser.chosen('" + 
					selection.getSingleItem().getObjectId() + "')");
		}
	}

}

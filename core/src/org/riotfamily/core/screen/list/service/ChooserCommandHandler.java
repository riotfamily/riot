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
		
		@Override
		protected String getIcon(String action) {
			return "accept";
		}
		
		public CommandResult execute(CommandContext context, Selection selection) {
			return new ScriptResult("parent.riot.chooser.chosen('" + 
					selection.getSingleItem().getObjectId() + "')");
		}
	}

}

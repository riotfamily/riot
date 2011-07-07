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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.util.Generics;
import org.riotfamily.core.screen.DefaultScreenContext;
import org.riotfamily.core.screen.ListScreen;
import org.riotfamily.core.screen.ScreenContext;
import org.riotfamily.core.screen.ScreenLink;
import org.riotfamily.core.screen.ScreenUtils;
import org.riotfamily.core.screen.list.ChooserSettings;
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

	@Override
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
		
		@Override
		protected String getAction() {
			return null;
		}
		
		@Override
		protected String getIcon() {
			return "bullet_go";
		}
		
		public CommandResult execute(CommandContext context, Selection selection) {
			ScreenContext nextContext = new DefaultScreenContext(nextList, 
					null, selection.getSingleItem().getObject(), false, screenContext);
			
			ChooserSettings settings = new ChooserSettings(chooserTarget.getId(), null, state.getChooserSettings().getTargetClass());
			ScreenLink link = settings.appendTo(nextContext.getLink());
			return new GotoUrlResult(context.getRequest(), link.getUrl());
		}
	}
	
	private class ChooseCommand extends AbstractCommand {
		
		@Override
		public boolean isEnabled(CommandContext context, Selection selection) {
			Class<?> targetClass = state.getChooserSettings().getTargetClass();
			return selection.size() == 1 
				&& targetClass.isAssignableFrom(selection.getSingleItem().getObject().getClass());

		}
		
		@Override
		protected String getAction() {
			return null;
		}
		
		@Override
		protected String getIcon() {
			return "accept";
		}
		
		public CommandResult execute(CommandContext context, Selection selection) {
			return new ScriptResult("parent.riot.window.getDialog(window).options.chooser.chosen('" + 
					selection.getSingleItem().getObjectId() + "')");
		}
	}

}

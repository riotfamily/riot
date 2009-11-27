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
package org.riotfamily.core.screen.list.command.impl;

import java.util.Collection;

import org.riotfamily.core.screen.RiotScreen;
import org.riotfamily.core.screen.ScreenContext;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandInfo;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.impl.support.AbstractSingleItemCommand;
import org.riotfamily.core.screen.list.command.result.GotoUrlResult;
import org.springframework.util.Assert;

public class GoToNestedCommand extends AbstractSingleItemCommand<Object> {

	private String screenId;
	
	public void setScreenId(String screenId) {
		this.screenId = screenId;
	}
	
	@Override
	protected String getIcon() {
		return "application_go";
	}
	
	@Override
	protected String getAction() {
		return null;
	}
	
	private RiotScreen findChildScreen(CommandContext context) {
		RiotScreen itemScreen = context.getScreen().getItemScreen();
		Assert.notNull(itemScreen, "The list must have an itemScreen");
		
		Collection<RiotScreen> childScreens = itemScreen.getChildScreens();
		Assert.notEmpty(childScreens, "The itemScreen must have childScreens");
		
		for (RiotScreen child : childScreens) {
			if (screenId == null || screenId.equals(child.getId())) {
				return child;
			}
		}
		throw new IllegalArgumentException("No such childScreen: " + screenId);
	}
	
	@Override
	public CommandInfo getInfo(CommandContext context) {
		RiotScreen screen = findChildScreen(context);
		ScreenContext screenContext = context.createNewItemContext(null);
		return new CommandInfo(
				getName(),
				getAction(), 
				screen.getTitle(screenContext), 
				getIconUrl(context, screen.getIcon()), 
				false);
	}
	
	@Override
	protected CommandResult execute(CommandContext context, Object item) {
		RiotScreen screen = findChildScreen(context);
		ScreenContext childContext = context.createItemContext(item).createChildContext(screen);
		return new GotoUrlResult(context.getRequest(), childContext.getUrl());
	}

	
}

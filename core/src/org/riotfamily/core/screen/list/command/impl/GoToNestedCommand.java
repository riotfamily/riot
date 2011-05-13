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
import java.util.Locale;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.core.screen.ListScreen;
import org.riotfamily.core.screen.RiotScreen;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandInfo;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.impl.support.AbstractSingleItemCommand;
import org.riotfamily.core.screen.list.command.result.GotoUrlResult;
import org.springframework.util.Assert;
import org.springframework.web.servlet.support.RequestContextUtils;

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
		
		if (itemScreen instanceof ListScreen) {
			return itemScreen;
		}
		
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
		String iconName = screen.getIcon();
		if (iconName == null) {
			iconName = getIcon();
		}
		return new CommandInfo(
				getName(),
				getAction(), 
				getTitle(screen.getId(), context), 
				getIconUrl(context, iconName), 
				false);
	}
	
	@Override
	protected CommandResult execute(CommandContext context, Object item) {
		if (context.getScreen().getItemScreen() instanceof ListScreen) {
			return new GotoUrlResult(context.getScreenContext()
					.createItemContext(item));
		}
		else {
			RiotScreen screen = findChildScreen(context);
			return new GotoUrlResult(context.getScreenContext()
				.createItemContext(item).createChildContext(screen));
		}
	}
	
	private String getTitle(String screenId, CommandContext context) {
		String code = "screen." + screenId;
		String defaultTitle = FormatUtils.xmlToTitleCase(screenId);
		Locale locale = RequestContextUtils.getLocale(context.getRequest());
		return context.getMessageResolver().getMessage(code, null, defaultTitle, locale);
	}

	
}

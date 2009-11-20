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
package org.riotfamily.pages.riot.command;

import org.riotfamily.core.screen.ScreenContext;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.impl.support.AbstractBatchCommand;
import org.riotfamily.core.screen.list.command.result.PopupResult;
import org.riotfamily.pages.mapping.PageResolver;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;
import org.riotfamily.pages.view.PageFacade;
import org.springframework.beans.factory.annotation.Required;

public class GotoVirtualPageCommand extends AbstractBatchCommand<Object> {
	
	private String pageType;

	@Required
	public void setPageType(String pageType) {
		this.pageType = pageType;
	}
	
	@Override
	protected boolean isShowOnForm(CommandContext context) {
		return true;
	}
	
	@Override
	protected String getIcon(String action) {
		return "application_go";
	}
	
	@Override
	protected String getName() {
		return "Goto Page";
	}

	@Override
	protected CommandResult execute(CommandContext context, Object object, 
			int index, int selectionSize) {
		
		ScreenContext ctx = context.createParentContext();
		Site site = null;
		while (site == null && ctx != null) {
			Object parent = ctx.getObject();
			if (parent instanceof Site) {
				site = (Site) parent;
			}
			ctx = ctx.createParentContext();
		}
		if (site == null) {
			site = Site.loadDefaultSite();
		}
		Page page = PageResolver.getVirtualPage(site, pageType, object);
		String url = new PageFacade(page, context.getRequest()).getUrl();
		return new PopupResult(url);
	}

}

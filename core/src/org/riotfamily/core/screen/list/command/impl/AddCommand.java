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

import org.riotfamily.core.dao.Constraints;
import org.riotfamily.core.dao.RiotDao;
import org.riotfamily.core.screen.ScreenContext;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.SelectionItem;
import org.riotfamily.core.screen.list.command.impl.support.AbstractChildCommand;
import org.riotfamily.core.screen.list.command.result.GotoUrlResult;

public class AddCommand extends AbstractChildCommand {
	
	@Override
	protected boolean isEnabled(CommandContext context, SelectionItem parent) {
		RiotDao dao = context.getScreen().getDao();
		if (dao instanceof Constraints) {
			Constraints cd = (Constraints) dao;
			return cd.canAdd(parent.getObject());
		}
		return true;
	}
	
	@Override
	protected CommandResult execute(CommandContext context, SelectionItem parent) {
		ScreenContext childContext = context.createNewItemContext(parent.getObject());
		return new GotoUrlResult(context.getRequest(), childContext.getUrl());
	}

}

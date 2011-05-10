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
package org.riotfamily.core.screen.list.command.impl.support;

import org.riotfamily.core.dao.Tree;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.SelectionItem;

public abstract class AbstractChildCommand extends AbstractCommand {

	@Override
	public final boolean isEnabled(CommandContext context, Selection selection) {
		if (selection.size() > 1) {
			return false;
		}
		return isEnabled(context, getParent(context, selection));
	}
	
	public final CommandResult execute(CommandContext context, Selection selection) {
		return execute(context, getParent(context, selection));
	}
	
	protected boolean isEnabled(CommandContext context, SelectionItem parent) {
		return true;
	}
	
	protected abstract CommandResult execute(CommandContext context, SelectionItem parent);

	
	private SelectionItem getParent(CommandContext context, Selection selection) {
		if (context.getScreen().getDao() instanceof Tree) {
			if (selection.size() == 1) {
				return selection.getSingleItem();
			}
		}
		return new RootSelectionItem();
	}
	
}

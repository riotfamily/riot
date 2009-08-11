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
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.SelectionItem;
import org.riotfamily.core.screen.list.command.impl.dialog.YesNoCommand;
import org.riotfamily.core.screen.list.command.result.BatchResult;
import org.riotfamily.core.screen.list.command.result.NotificationResult;
import org.riotfamily.core.screen.list.command.result.RefreshListResult;

public class DeleteCommand extends YesNoCommand {

	public boolean isEnabled(CommandContext context, Selection selection) {
		if (selection.size() > 0) {
			RiotDao dao = context.getScreen().getDao();
			if (dao instanceof Constraints) {
				Constraints cd = (Constraints) dao;
				for (SelectionItem item : selection) {
					if (!cd.canDelete(item.getObject())) {
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}

	@Override
	protected String[] getCodes(CommandContext context, Selection selection) {
		return new String[] {
			"confirm.delete." + context.getScreen().getId(),
			"confirm.delete." + context.getScreen().getDao().getEntityClass(),
			"confirm.delete"
		};
	}
	
	protected Object[] getArgs(CommandContext context, Selection selection) {
		String label = null;
		if (selection.size() == 1) {
			label = context.getScreen().getItemLabel(selection.getSingleItem().getObject());
		}
		return new Object[] { selection.size(),	label };
	}
	
	protected String getDefaultMessage(CommandContext context, Selection selection) {
		return "Do you really want to delete {0,choice,1#\"{1}\"|1<the{0} selected items}?";
	}
	
	@Override
	protected CommandResult handleYes(CommandContext context,
			Selection selection, Object input) {
	
		Object[] args = getArgs(context, selection);
		for (SelectionItem item : selection) {
			context.getScreen().getDao().delete(item.getObject(), context.getParent());
		}
		return new BatchResult(
			new RefreshListResult(),
			new NotificationResult(context, this)
				.setDefaultMessage("{0,choice,1#Item \"{1}\"|1<{0} items} successfully deleted.")
				.setArgs(args));
	}

}

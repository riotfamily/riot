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
package org.riotfamily.core.screen.list.command.impl.clipboard;

import org.riotfamily.core.dao.CopyAndPaste;
import org.riotfamily.core.screen.ListScreen;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.SelectionItem;
import org.riotfamily.core.screen.list.command.impl.support.AbstractCommand;
import org.riotfamily.core.screen.list.command.result.BatchResult;
import org.riotfamily.core.screen.list.command.result.NotificationResult;
import org.riotfamily.core.screen.list.command.result.UpdateCommandsResult;
import org.springframework.util.Assert;

public class CopyCommand extends AbstractCommand implements ClipboardCommand {

	@Override
	protected String getIcon() {
		return "page_copy";
	}
	
	@Override
	protected String getAction() {
		return "copy";
	}
	
	@Override
	public boolean isEnabled(CommandContext context, Selection selection) {
		if (selection.isEmpty()) {
			return false;
		}
		CopyAndPaste dao = getDao(context.getScreen());
		for (SelectionItem item : selection) {
			if (!dao.canCopy(item.getObject())) {
				return false;
			}
		}
		return true;
	}
	
	private CopyAndPaste getDao(ListScreen screen) {
		Assert.isInstanceOf(CopyAndPaste.class, screen.getDao());
		return (CopyAndPaste) screen.getDao();
	}
	
	public CommandResult execute(CommandContext context, Selection selection) {
		Clipboard.get(context).set(context.getScreen(), selection, this);
		return new BatchResult(
				new UpdateCommandsResult(),
				new NotificationResult(context, this)
					.setArgs(selection.size())
					.setDefaultMessage("{0,choice,1#Item|1<{0} items} put into the clipboard"));
	}
	
	public boolean canPaste(ListScreen source, Selection selection, 
			CommandContext context, SelectionItem parentItem) {

		CopyAndPaste dao = getDao(context.getScreen());
		Object parent = getParent(parentItem, context);
		
		for (SelectionItem item : selection) {
			if (!dao.canPasteCopy(item.getObject(), parent)) {
				return false;
			}
		}
		return true;
	}

	public void paste(ListScreen source, Selection selection, 
			CommandContext context, SelectionItem parentItem, 
			NotificationResult notification) {
		
		CopyAndPaste dao = getDao(context.getScreen());
		Object parent = getParent(parentItem, context);

		for (SelectionItem item : selection) {
			dao.pasteCopy(item.getObject(), parent);
		}
		
		notification.setDefaultMessage("{0,choice,1#Item has|1<{0} items have} been copied.");
	}

	private Object getParent(SelectionItem parentItem, CommandContext context) {
		Object parent = parentItem.getObject();
		if (parent == null) {
			parent = context.getParent();
		}
		return parent;
	}
}

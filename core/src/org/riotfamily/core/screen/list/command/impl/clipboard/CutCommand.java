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

import java.util.List;

import org.riotfamily.common.util.Generics;
import org.riotfamily.core.dao.CutAndPaste;
import org.riotfamily.core.dao.Hierarchy;
import org.riotfamily.core.dao.RiotDao;
import org.riotfamily.core.dao.Tree;
import org.riotfamily.core.screen.ListScreen;
import org.riotfamily.core.screen.ScreenContext;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.SelectionItem;
import org.riotfamily.core.screen.list.command.impl.support.AbstractCommand;
import org.riotfamily.core.screen.list.command.result.BatchResult;
import org.riotfamily.core.screen.list.command.result.NotificationResult;
import org.riotfamily.core.screen.list.command.result.UpdateCommandsResult;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public class CutCommand extends AbstractCommand implements ClipboardCommand {

	private boolean cutBeforePaste = false;
	
	public void setCutBeforePaste(boolean cutBeforePaste) {
		this.cutBeforePaste = cutBeforePaste;
	}
	
	@Override
	protected String getAction() {
		return "cut";
	}
	
	@Override
	public boolean isEnabled(CommandContext context, Selection selection) {
		CutAndPaste dao = getDao(context.getScreen());
		if (selection.size() == 0) {
			return false;
		}
		if (!allItemsHaveSameParent(selection)) {
			return false;
		}
		for (SelectionItem item : selection) {
			if (!dao.canCut(item.getObject())) {
				return false;
			}
		}
		return true;
	}

	private CutAndPaste getDao(ListScreen screen) {
		Assert.isInstanceOf(CutAndPaste.class, screen.getDao());
		return (CutAndPaste) screen.getDao();
	}
	
	private boolean allItemsHaveSameParent(Selection selection) {
		String parentNodeId = selection.getFirstItem().getParentNodeId();
		for (SelectionItem item : selection) {
			if (!ObjectUtils.nullSafeEquals(parentNodeId, item.getParentNodeId())) {
				return false;
			}
		}
		return true;
	}
	
	private List<Object> getAncestors(CommandContext context, SelectionItem item) {
		List<Object> ancestors = Generics.newArrayList();
		RiotDao dao = context.getScreen().getDao();
		Object parent = item.getObject();
		if (parent != null && dao instanceof Tree) {
			Tree tree = (Tree) dao;
			while (parent != null) {
				ancestors.add(parent);
				parent = tree.getParentNode(parent);
			}
		}
		else {
			ScreenContext ctx = context.getScreenContext().createParentContext();
			while (ctx != null) {
				ancestors.add(ctx.getObject());
				ctx = ctx.createParentContext();
			}
		}
		return ancestors;
	}

	public CommandResult execute(CommandContext context, Selection selection) {
		Clipboard.get(context).set(context.getScreen(), selection, this);
		return new BatchResult(
				new UpdateCommandsResult(),
				new NotificationResult(context, this)
					.setArgs(selection.size())
					.setDefaultMessage("{0,choice,1#Item|1<{0} items} put into the clipboard"));
	}
	
	/**
	 * Checks whether the selection can be pasted to the new parent.
	 */
	public boolean canPaste(ListScreen source, Selection selection, 
			CommandContext context, SelectionItem parentItem) {
		
		CutAndPaste dao = getDao(context.getScreen());
		Object parent = getNewParent(parentItem, context);
		
		List<Object> ancestors = getAncestors(context, parentItem);
		for (SelectionItem item : selection) {
			if (ancestors.contains(item.getObject())
					|| !dao.canPasteCut(item.getObject(), parent)) {
				
				return false;
			}
		}
		return true;
	}
	
	public void paste(ListScreen source, Selection selection, 
			CommandContext context, SelectionItem parentItem, 
			NotificationResult notification) {
		
		CutAndPaste sourceDao = getDao(source);
		CutAndPaste targetDao = getDao(context.getScreen());
		Object newParent = getNewParent(parentItem, context);
		
		int count = 0;
		for (SelectionItem item : selection) {
			Object obj = item.getObject();
			Object oldParent = getOldParent(obj, sourceDao);
			if (!ObjectUtils.nullSafeEquals(oldParent, newParent)) {
				if (cutBeforePaste) {
					sourceDao.cut(obj, oldParent);
					targetDao.pasteCut(obj, newParent);
				}
				else {
					targetDao.pasteCut(obj, newParent);
					sourceDao.cut(obj, oldParent);
				}
				count++;
			}
		}
		notification.setArgs(count).setDefaultMessage(
				"{0,choice,0#No items have|1#Item has|1<{0} items have} been moved.");
	}
	
	private Object getOldParent(Object obj, Hierarchy dao) {
		return dao.getParent(obj);
	}
	
	private Object getNewParent(SelectionItem parentItem, CommandContext context) {
		Object parent = parentItem.getObject();
		if (parent == null) {
			parent = context.getParent();
		}
		return parent;
	}
}

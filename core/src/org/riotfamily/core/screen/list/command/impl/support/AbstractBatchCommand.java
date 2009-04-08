/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Riot.
 *
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.core.screen.list.command.impl.support;

import org.riotfamily.common.util.Generics;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.SelectionItem;
import org.riotfamily.core.screen.list.command.result.BatchResult;

public abstract class AbstractBatchCommand<T> extends AbstractCommand {

private Class<T> requiredType;
	
	@SuppressWarnings("unchecked")
	private Class<T> getRequiredType() {
		if (requiredType == null) { 
			requiredType = (Class<T>) Generics.getTypeArguments(
					AbstractBatchCommand.class, getClass()).get(0);
		}
		return requiredType; 
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public final boolean isEnabled(CommandContext context, Selection selection) {
		if (selection.size() < 1) {
			return false;
		}
		int i = 0;
		for (SelectionItem item : selection) {
			Object obj = item.getObject();
			if (!getRequiredType().isInstance(obj)) {
				return false;
			}
			if (!isEnabled(context, (T) obj, i++, selection.size())) {
				return false;
			}
		}
		return true;
	}
		
	protected boolean isEnabled(CommandContext context, T item, 
			int index, int selectionSize) {
		
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public final CommandResult execute(CommandContext context, Selection selection) {
		BatchResult result = new BatchResult();
		int i = 0;
		for (SelectionItem item : selection) {
			result.add(execute(context, (T) item.getObject(), 
					i++, selection.size()));
		}
		return result;
	}
	
	protected abstract CommandResult execute(CommandContext context, 
			T item, int index, int selectionSize);
	
}

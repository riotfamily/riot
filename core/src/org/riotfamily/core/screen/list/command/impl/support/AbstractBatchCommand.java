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

import org.riotfamily.common.util.GenericClassTypeResolver;
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
			requiredType = (Class<T>) GenericClassTypeResolver.resolveTypeArgument(getClass(), AbstractBatchCommand.class);
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

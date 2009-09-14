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

import org.riotfamily.common.util.Generics;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.Selection;

public abstract class AbstractSingleItemCommand<T> extends AbstractCommand {

	private Class<T> requiredType;
	
	@SuppressWarnings("unchecked")
	private Class<T> getRequiredType() {
		if (requiredType == null) { 
			requiredType = (Class<T>) Generics.getTypeArguments(
					AbstractSingleItemCommand.class, getClass()).get(0);
		}
		return requiredType; 
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	public final boolean isEnabled(CommandContext context, Selection selection) {
		if (selection.size() == 1 && getRequiredType().isInstance(
				selection.getSingleItem().getObject())) {
			
			return isEnabled(context, (T) selection.getSingleItem().getObject());
		}
		return false;
	}
	
	protected boolean isEnabled(CommandContext context, T item) {
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public final CommandResult execute(CommandContext context, Selection selection) {
		return execute(context, (T) selection.getSingleItem().getObject());
	}
	
	protected abstract CommandResult execute(CommandContext context, T item);
}

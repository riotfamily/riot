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
package org.riotfamily.core.screen.list.command;

import org.riotfamily.common.util.Generics;
import org.riotfamily.core.screen.list.command.result.CommandResult;

public abstract class SingleItemCommand<T> extends AbstractCommand {

	private Class<T> requiredType;
	
	@SuppressWarnings("unchecked")
	private Class<T> getRequiredType() {
		if (requiredType == null) { 
			requiredType = (Class<T>) Generics.getTypeArguments(
					SingleItemCommand.class, getClass()).get(0);
		}
		return requiredType; 
	}
	
	@Override
	public boolean isEnabled(CommandContext context, Selection selection) {
		if (selection.size() == 1 && selection.isCompatible(getRequiredType())) {
			T item = selection.getSingleObject(getRequiredType());
			return isEnabled(context, item);
		}
		return false;
	}
	
	protected boolean isEnabled(CommandContext context, T item) {
		return true;
	}
	
	public CommandResult execute(CommandContext context, Selection selection) {
		return execute(context, selection.getSingleObject(getRequiredType()));
	}
	
	protected abstract CommandResult execute(CommandContext context, T item);
}

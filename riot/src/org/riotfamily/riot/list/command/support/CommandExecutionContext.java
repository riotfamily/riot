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
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.list.command.support;

import org.riotfamily.riot.editor.EditorDefinitionUtils;
import org.riotfamily.riot.list.ui.Constants;
import org.riotfamily.riot.list.ui.ListContext;
import org.springframework.web.bind.ServletRequestUtils;

/**
 * CommandContext used for command execution. 
 */
public class CommandExecutionContext extends AbstractCommandContext {

	private String objectId;

	private int rowIndex;
	
	private Object item;
	
	private boolean confirmed;
	
	public CommandExecutionContext(ListContext context) {
		super(context);
		this.objectId = context.getObjectId();
		this.rowIndex = context.getRowIndex();
		this.confirmed = ServletRequestUtils.getBooleanParameter(
				context.getRequest(), Constants.PARAM_CONFIRMED, false);
	}

	public String getObjectId() {
		return objectId;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public Object getItem() {
		if (item == null) {
			if (objectId != null) {
				item = getDao().load(objectId);
			}
			else if (getParentId() != null) {
				item = EditorDefinitionUtils.loadParent(getListDefinition(), 
						getParentId());
			}
		}
		return item;
	}
	
	public boolean isConfirmed() {
		return this.confirmed;
	}
	
}

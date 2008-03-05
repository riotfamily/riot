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
 * Portions created by the Initial Developer are Copyright (C) 2008
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.riot.command;

import org.riotfamily.pages.model.Page;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.core.AbstractCommand;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class PublishStatus extends AbstractCommand {

	protected boolean isEnabled(CommandContext context, String action) {
		return false;
	}
	
	protected String getStyleClass(CommandContext context, String action) {
		if (PageCommandUtils.isTranslated(context)) {
			Page page = PageCommandUtils.getPage(context);
			if (!page.isPublished()) {
				return "new";
			}
			if (page.isDirty()) {
				return "dirty";
			}
			return "published";
		}
		return "translatable";
	}
	
	public CommandResult execute(CommandContext context) {
		return null;
	}

}

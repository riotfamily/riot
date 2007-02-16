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
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.page.command;

import org.riotfamily.pages.page.Page;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.result.ReloadResult;
import org.riotfamily.riot.list.command.support.AbstractCommand;

/**
 * List command to toggle the publish-state of a page. 
 */
public class PublishPageCommand extends AbstractCommand {
	
	public static final String ACTION_PUBLISH = "publishPage";
	
	public static final String ACTION_UNPUBLISH = "unpublishPage";
	
	public String getConfirmationMessage(CommandContext context) {
		
		Class clazz = context.getListDefinition().getBeanClass();
		Page page = (Page) context.getBean();
		
		String type = context.getMessageResolver().getClassLabel(null, clazz);
		String label = context.getListDefinition().getDisplayDefinition().getLabel(page);
		
		Object[] args = new Object[] {label, type, context.getObjectId()};
		
		if (page.isPublished()) {
			return context.getMessageResolver().getMessage("confirm.unpublish", 
					args, "Do you really want to unpublish this page?");
		}
		else {
			return context.getMessageResolver().getMessage("confirm.publish", 
					args, "Do you really want to publish this page?");
		}
	}
	
	public CommandResult execute(CommandContext context) {
		Page page = (Page) context.getBean();
		page.setPublished(!page.isPublished());
		context.getDao().update(page);
		return new ReloadResult();
	}

	public String getAction(CommandContext context) {
		Page page = (Page) context.getBean();
		return page.isPublished() ? ACTION_UNPUBLISH : ACTION_PUBLISH; 
	}
	
	protected String getLabelKeySuffix(CommandContext context) {
		return getAction(context);
	}

}

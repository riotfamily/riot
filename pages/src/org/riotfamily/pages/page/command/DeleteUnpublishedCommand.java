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
 *   alf
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.page.command;

import org.riotfamily.common.util.PropertyUtils;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.core.DeleteCommand;

/**
 * @author Alf Werder [alf dot werder at artundweise dot de]
 * @since 6.4
 */
public class DeleteUnpublishedCommand extends DeleteCommand {
	private String publishedProperty = "published";
	private String action = "delete";
	
	public DeleteUnpublishedCommand() {}

	public boolean isEnabled(CommandContext context) {
		boolean published = true;
		Object bean = context.getBean();
		
		if (bean != null) {
			published = ((Boolean) PropertyUtils.getProperty(
					bean, publishedProperty)).booleanValue();
		}
		
		return !published && super.isEnabled(context);
	}

	public void setPublishedProperty(String publishedProperty) {
		this.publishedProperty = publishedProperty;
	}
	
	public void setAction(String action) {
		this.action = action;
	}

	public String getAction(CommandContext context) {
		return action;
	}
}

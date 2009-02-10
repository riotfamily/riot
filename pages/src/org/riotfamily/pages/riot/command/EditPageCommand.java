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
package org.riotfamily.pages.riot.command;

import org.riotfamily.pages.model.Page;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.core.EditCommand;

public class EditPageCommand extends EditCommand {

	@Override
	public boolean isEnabled(CommandContext context) {
		return PageCommandUtils.isLocalPage(context);
	}

	public String getItemStyleClass(CommandContext context) {
		Page page = PageCommandUtils.getPage(context);
		StringBuffer styleClasses = new StringBuffer();
		if (page.getNode().isSystemNode()) {
			appendStyleClass(styleClasses, "system");
		}
		if (page.getNode().isHidden()) {
			appendStyleClass(styleClasses, "hidden-node");
		}
		if (page.isHidden()) {
			appendStyleClass(styleClasses, "hidden");
		}
		if (page.isFolder()) {
			appendStyleClass(styleClasses, "folder");
		}
		if (page.isDirty()) {
			appendStyleClass(styleClasses, "dirty");
		}
		if (page.isPublished()) {
			appendStyleClass(styleClasses, "published");
		}
		return styleClasses.toString();
	}

	private void appendStyleClass(StringBuffer buffer, String styleClass) {
		if (buffer.length() > 0) {
			buffer.append(" ");
		}
		buffer.append(styleClass);
	}

}

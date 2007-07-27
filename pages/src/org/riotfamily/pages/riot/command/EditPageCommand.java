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

import java.util.Locale;

import org.riotfamily.pages.Page;
import org.riotfamily.pages.PageValidationUtils;
import org.riotfamily.pages.dao.PageDao;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.core.EditCommand;
import org.riotfamily.riot.list.command.result.ShowListResult;

public class EditPageCommand extends EditCommand {

	public static final String ACTION_TRANSLATE = "translate";

	private PageDao pageDao;


	public EditPageCommand(PageDao pageDao) {
		this.pageDao = pageDao;
	}

	protected String getAction(CommandContext context) {
		return PageCommandUtils.isTranslated(context)
				? super.getAction(context)
				: ACTION_TRANSLATE;
	}

	protected boolean isEnabled(CommandContext context, String action) {
		if (action == ACTION_TRANSLATE) {
			Page page = PageCommandUtils.getPage(context);
			Locale locale = PageCommandUtils.getParentLocale(context);
			return PageValidationUtils.isTranslatable(page, locale);
		}
		return true;
	}

	protected String getItemStyleClass(CommandContext context, String action) {
		StringBuffer styleClasses = new StringBuffer();
		if (action == ACTION_TRANSLATE) {
			appendStyleClass(styleClasses, "foreign-page");
		}
		/* TODO: It would be better to set these styles in a different location. */
		if (PageCommandUtils.isSystemPage(context)) {
			appendStyleClass(styleClasses, "system-page");
		}
		if (PageCommandUtils.isHiddenNode(context)) {
			appendStyleClass(styleClasses, "hidden-node");
		}
		if (PageCommandUtils.isHiddenPage(context)) {
			appendStyleClass(styleClasses, "hidden-page");
		}
		if (PageCommandUtils.isFolder(context)) {
			appendStyleClass(styleClasses, "folder");
		}
		return styleClasses.toString();
	}

	private void appendStyleClass(StringBuffer buffer, String styleClass) {
		if (buffer.length() > 0) {
			buffer.append(" ");
		}
		buffer.append(styleClass);
	}

	public CommandResult execute(CommandContext context) {
		if (PageCommandUtils.isTranslated(context)) {
			return super.execute(context);
		}
		Page page = PageCommandUtils.getPage(context);
		Locale locale = PageCommandUtils.getParentLocale(context);
		pageDao.addTranslation(page, locale);
		return new ShowListResult(context);
	}

}

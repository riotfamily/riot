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

import org.riotfamily.pages.Page;
import org.riotfamily.pages.PageNode;
import org.riotfamily.pages.PageValidationUtils;
import org.riotfamily.pages.Site;
import org.riotfamily.pages.dao.PageDao;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.core.Clipboard;
import org.riotfamily.riot.list.command.core.PasteCommand;

public class PastePageCommand extends PasteCommand {

	private PageDao pageDao;

	public PastePageCommand(PageDao pageDao) {
		this.pageDao = pageDao;
	}

	protected boolean isEnabled(CommandContext context, String action) {
		boolean enabled = super.isEnabled(context, action);
		Clipboard cb = Clipboard.get(context);
		Object obj = cb.getObject();
		if (obj instanceof Page) {
			Page page = (Page) obj;
			enabled &= isValidChild(context.getParent(), page);

			//REVISIT  
			// Only allow pasting into the same site
			Site site = PageCommandUtils.getParentSite(context);
			if(site != null) {
				enabled &= page.getSite().equals(site);
			}
		}
		return enabled;
	}

	private boolean isValidChild(Object parent, Page page) {
		PageNode parentNode;
		if (parent instanceof Page) {
			parentNode = ((Page) parent).getNode();
		}
		else {
			parentNode = pageDao.getRootNode();
		}
		return PageValidationUtils.isValidChild(parentNode, page);
	}

}

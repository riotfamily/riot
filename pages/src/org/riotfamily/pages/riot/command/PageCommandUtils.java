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
import org.riotfamily.pages.model.Site;
import org.riotfamily.riot.list.command.CommandContext;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public final class PageCommandUtils {

	private PageCommandUtils() {
	}

	public static Page getPage(CommandContext context) {
		return (Page) context.getBean();
	}

	public static Site getSite(CommandContext context) {
		return getPage(context).getSite();
	}

	/**
	 * If the parent cannot be determined the return will be <code>null</code>.
	 */
	public static Site getParentSite(CommandContext context) {
		Object parent = context.getParent();
		if (parent instanceof Page) {
			return ((Page) parent).getSite();
		}
		else if (parent instanceof Site) {
			return (Site) parent;
		}
		return null;
	}

	public static boolean isMasterLocale(CommandContext context) {
		return getSite(context).getMasterSite() == null;
	}

	public static boolean isMasterLocaleList(CommandContext context) {
		Site parentSite = getParentSite(context);
		return parentSite == null || parentSite.getMasterSite() == null;
	}

	public static boolean hasTranslation(CommandContext context) {
		return getPage(context).getNode().getPages().size() > 1;
	}

	public static boolean isLocalPage(CommandContext context) {
		Page page = getPage(context);
		Site parentSite = getParentSite(context);
		return parentSite == null || parentSite.equals(page.getSite());
	}

	public static boolean isSystemPage(CommandContext context) {
		return getPage(context).getNode().isSystemNode();
	}

}

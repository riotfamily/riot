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
package org.riotfamily.core.screen;

import org.riotfamily.core.dao.RiotDao;

public final class ScreenUtils {

	private ScreenUtils() {
	}
	
	public static ListScreen getListScreen(RiotScreen screen) {
		while (screen != null) {
			if (screen instanceof ListScreen) {
				return (ListScreen) screen;
			}
			screen = screen.getParentScreen();
		}
		return null;
	}
	
	public static RiotDao getDao(RiotScreen screen) {
		ListScreen listScreen = getListScreen(screen);
		if (listScreen != null) {
			return listScreen.getDao();
		}
		return null;
	}
	
	public static ListScreen getParentListScreen(RiotScreen screen) {
		ListScreen list = getListScreen(screen);
		if (list != null) {
			return getListScreen(list.getParentScreen());
		}
		return null;
	}
	
	public static ListScreen getParentListScreen(RiotScreen screen, 
			ScreenContext context) {
		
		if (context.isNestedTreeItem()) {
			return getListScreen(screen);
		}
		return getParentListScreen(screen);
	}
	
	public static String getLabel(Object object, RiotScreen screen) {
		return getListScreen(screen).getItemLabel(object);
	}
	
	public static Object loadParent(RiotScreen screen, ScreenContext context) {
		if (context.getParentId() != null) {
			ListScreen listScreen = getParentListScreen(screen, context);
			return listScreen.getDao().load(context.getParentId());
		}
		return null;
	}
}

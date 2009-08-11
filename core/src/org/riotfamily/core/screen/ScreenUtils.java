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

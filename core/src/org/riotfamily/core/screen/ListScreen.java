package org.riotfamily.core.screen;

import org.riotfamily.core.dao.RiotDao;

public interface ListScreen extends RiotScreen {

	public RiotDao getDao();

	public RiotScreen getItemScreen();

	public String getItemLabel(Object object);
	
	public String getListStateKey(ScreenContext screenContext);

}
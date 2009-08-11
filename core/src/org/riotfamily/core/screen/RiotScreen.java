package org.riotfamily.core.screen;

import java.util.Collection;


public interface RiotScreen {

	public RiotScreen getParentScreen();
	
	public Collection<RiotScreen> getChildScreens();
	
	public Collection<Screenlet> getScreenlets();
	
	public void setParentScreen(RiotScreen parentScreen);

	public String getId();
	
	public String getIcon();
	
	public String getTitle(ScreenContext context);
	
}

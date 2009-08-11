package org.riotfamily.core.screen;

import java.util.Collection;
import java.util.Map;

import org.riotfamily.common.util.Generics;
import org.springframework.util.Assert;

public class ScreenRepository {

	private Map<String, RiotScreen> screenMap = Generics.newHashMap();
	
	private RiotScreen rootScreen;
	
	public void setRootScreen(RiotScreen screen) {
		this.rootScreen = screen;
		screenMap.clear();
		screenMap.put(screen.getId(), screen);
		registerScreens(screen.getChildScreens());
	}
	
	private void registerScreens(Collection<RiotScreen> screens) {
		if (screens != null) {
			for (RiotScreen screen : screens) {
				if (screenMap.containsKey(screen.getId())) {
					throw new IllegalArgumentException(
							"A screen with the same id already exists: "
							+ screen.getId());
				}
				screenMap.put(screen.getId(), screen);
				registerScreens(screen.getChildScreens());
			}
		}
	}
	
	public RiotScreen getScreen(String id) {
		if (id == null) {
			return rootScreen;
		}
		return screenMap.get(id);
	}
	
	@SuppressWarnings("unchecked")
	public<T extends RiotScreen> T getScreen(String id, Class<T> requiredType) {
		RiotScreen screen = screenMap.get(id);
		Assert.isInstanceOf(requiredType, screen);
		return (T) screen;
	}

}

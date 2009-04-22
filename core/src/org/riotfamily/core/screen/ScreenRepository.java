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

import java.util.Collection;
import java.util.Map;

import org.riotfamily.common.util.Generics;
import org.riotfamily.common.util.SpringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

public class ScreenRepository implements ApplicationContextAware {

	private Map<String, RiotScreen> screenMap = Generics.newHashMap();
	
	private RiotScreen rootScreen;
	
	public void setApplicationContext(ApplicationContext applicationContext) {
		registerScreens(SpringUtils.listBeansOfType(
				applicationContext, RiotScreen.class));
	}
	
	private void registerScreens(Collection<RiotScreen> screens) {
		if (screens != null) {
			for (RiotScreen screen : screens) {
				screenMap.put(screen.getId(), screen);
				if (screen.getParentScreen() == null && rootScreen == null) {
					rootScreen = screen;
				}
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

/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.core.screen;

import java.util.Collection;
import java.util.Map;

import org.riotfamily.common.util.Generics;
import org.riotfamily.core.screen.form.FormScreen;
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
		Assert.notNull(screen, "No such screen: " + id);
		Assert.isInstanceOf(requiredType, screen);
		return (T) screen;
	}

	
	public FormScreen findFormScreen(String formId) {
		for (RiotScreen screen : screenMap.values()) {
			if (screen instanceof FormScreen) {
				FormScreen formScreen = (FormScreen) screen;
				if (formScreen.contains(formId)) {
					return formScreen;
				}
			}
		}
		return null;
	}
}

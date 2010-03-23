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
package org.riotfamily.forms2.base;

import java.util.List;

import org.riotfamily.common.util.Generics;
import org.riotfamily.forms2.client.Action;
import org.riotfamily.forms2.client.Html;

public class UserInterface {

	private List<Action> actions = Generics.newArrayList();
	
	public UserInterface() {
	}
	
	public List<Action> getActions() {
		return actions;
	}
	
	Action action(ElementState state, String selector, String command) {
		Action action = new Action(state.getId(), selector, command);
		actions.add(action);
		return action;
	}
	
	Action each(ElementState state, String selector, String method, Object... args) {
		return action(state, null, "each").set("selector", selector)
				.set("method", method).set("args", args);
	}

	public void invoke(ElementState state, String selector, String method, Object... args) {
		action(state, selector, "invoke").set("method", method).set("args", args);
	}
	
	public void update(ElementState state, String selector, Html html) {
		invoke(state, selector, "update", html);
	}
	
	public void replace(ElementState state, String selector, Html html) {
		invoke(state, selector, "replace", html);
	}

	public void insert(ElementState state, String selector, Html html) {
		invoke(state, selector, "insert", html);
	}
	
	public void remove(ElementState state, String selector) {
		invoke(state, selector, "remove");
	}
	
	public void moveUp(ElementState state, String selector) {
		action(state, selector, "moveUp");
	}
	
	public void moveDown(ElementState state, String selector) {
		action(state, selector, "moveDown");
	}
	
	public void addClassName(ElementState state, String selector, String className) {
		each(state, selector, "addClassName", className);
	}
	
	public void removeClassName(ElementState state, String selector, String className) {
		each(state, selector, "removeClassName", className);
	}
	
	public void schedule(ElementState state, String handler, String value, long millis) {
		action(state, null, "schedule")
			.set("handler", handler)
			.set("value", value)
			.set("millis", millis);
	}
	
}

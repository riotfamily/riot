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
package org.riotfamily.forms.base;

import java.util.List;

import org.riotfamily.common.util.Generics;
import org.riotfamily.forms.client.Action;
import org.riotfamily.forms.client.Html;
import org.riotfamily.forms.client.Resources;
import org.riotfamily.forms.client.Update;
import org.springframework.util.StringUtils;

/**
 * Class that allows event handlers to incrementally update the UI.
 */
public class UserInterface {

	private List<Action> actions = Generics.newArrayList();
	
	private FormState formState;
	
	public UserInterface(FormState formState) {
		this.formState = formState;
	}
	
	public Update getUpdate() {
		return new Update(new Resources(), actions);
	}
	
	/**
	 * Updates an element by replacing its content.
	 */
	public void update(Element.State state, String selector, Html html) {
		invoke(state, selector, "html", html);
		eval(html.extractScripts());
	}
	
	/**
	 * Replaces an element.
	 */
	public void replace(Element.State state, String selector, Html html) {
		invoke(state, selector, "replaceWith", html);
		eval(html.extractScripts());
	}
	
	public void refresh(Element.State state) {
		Html html = state.getFormState().newHtml();
		state.render(html);
		replace(state, null, html);
		state.handleStateEvent(new StateEvent(state, "refresh", this));
	}

	/**
	 * Appends content to an element.
	 */
	public void insert(Element.State state, String selector, Html html) {
		invoke(state, selector, "append", html);
		eval(html.extractScripts());
	}
	
	/**
	 * Removes an element.
	 */
	public void remove(Element.State state, String selector) {
		invoke(state, selector, "remove");
	}
	
	/**
	 * Moves an element before its previous sibling.
	 */
	public void moveUp(Element.State state, String selector) {
		invoke(state, selector, "moveUp");
	}
	
	/**
	 * Moves an element after its next sibling.
	 */
	public void moveDown(Element.State state, String selector) {
		invoke(state, selector, "moveDown");
	}
	
	/**
	 * Adds a CSS class to all matching elements.
	 */
	public void addClassName(Element.State state, String selector, String className) {
		invoke(state, selector, "addClass", className);
	}
	
	/**
	 * Removes a CSS class from all matching elements.
	 */
	public void removeClassName(Element.State state, String selector, String className) {
		invoke(state, selector, "removeClass", className);
	}
	
	/**
	 */
	public void invoke(Element.State state, String selector, String method, Object... args) {
		action(state.id(), selector, "invoke").set("method", method).set("args", args);
	}
	
	/**
	 * Schedules the submission of a synthetic event.
	 */
	public void schedule(Element.State state, String handler, String value, long millis) {
		action(state.id(), null, "schedule")
			.set("handler", handler)
			.set("value", value)
			.set("millis", millis);
	}
	
	public void eval(String script) {
		if (StringUtils.hasText(script)) {
			action(null, null, "eval").set("script", script);
		}
	}
	
	Action action(String id, String selector, String command) {
		Action action = new Action(id, selector, command);
		actions.add(action);
		return action;
	}
	
}

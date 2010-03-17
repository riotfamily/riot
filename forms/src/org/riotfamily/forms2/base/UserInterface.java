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
import org.riotfamily.forms2.client.HtmlAction;
import org.riotfamily.forms2.client.ScheduleAction;

public class UserInterface {

	private List<Action> actions = Generics.newArrayList();
	
	public UserInterface() {
	}
	
	public List<Action> getActions() {
		return actions;
	}
	
	public Html update(ElementState state, String selector) {
		return addAction(state, selector, "update");
	}
	
	public Html replace(ElementState state, String selector) {
		return addAction(state, selector, "replace");
	}

	public Html insert(ElementState state, String selector) {
		return addAction(state, selector, "insert");
	}
	
	public void remove(ElementState state, String selector) {
		actions.add(new Action(state.getId(), selector, "remove"));
	}
	
	public void schedule(ElementState state, String handler, String value, long millis) {
		actions.add(new ScheduleAction(state.getId(), handler, value, millis));
	}
	
	private Html addAction(ElementState state, String selector, String command) {
		Html html = state.getFormState().newHtml();
		actions.add(new HtmlAction(state.getId(), selector, command, html));
		return html;
	}

}

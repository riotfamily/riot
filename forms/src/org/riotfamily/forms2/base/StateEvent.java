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


public class StateEvent {

	private Element.State source;
	
	private String type;
	
	private boolean stopped;
	
	private final UserInterface ui;

	public StateEvent(Element.State source, String type, UserInterface ui) {
		this.source = source;
		this.type = type;
		this.ui = ui;
	}

	public Element.State getSource() {
		return source;
	}

	public String getType() {
		return type;
	}

	public UserInterface getUserInterface() {
		return ui;
	}
	
	public boolean isStopped() {
		return stopped;
	}

	public void stop() {
		stopped = true;
	}
	
}

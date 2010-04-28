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

import org.riotfamily.forms2.value.Value;

public class StateEvent {

	private ElementState source;
	
	private String type;
	
	private boolean stopped;
	
	private Object value;
	
	private boolean valuePopulated;

	private final UserInterface ui;

	public StateEvent(ElementState source, String type, UserInterface ui) {
		this.source = source;
		this.type = type;
		this.ui = ui;
	}

	public ElementState getSource() {
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
	
	public Object getValue() {
		if (!valuePopulated) {
			Value temp = new Value();
			source.populate(temp);
			value = temp.get();
			valuePopulated = true;
		}
		return value;
	}

}

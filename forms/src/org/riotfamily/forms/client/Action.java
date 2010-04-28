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
package org.riotfamily.forms.client;

import java.util.Map;

import org.riotfamily.common.util.Generics;

public class Action {
	
	private String id;
	
	private String selector;
	
	private String command;
	
	private Map<String, Object> data = Generics.newHashMap();
	
	public Action(String id, String selector, String command) {
		this.id = id;
		this.selector = selector;
		this.command = command;
	}
	
	public Action set(String name, Object value) {
		data.put(name, value);
		return this;
	}
			
	public String getId() {
		return id;
	}

	public String getSelector() {
		return selector;
	}

	public String getCommand() {
		return command;
	}
	
	public Map<String, Object> getData() {
		return data;
	}
}
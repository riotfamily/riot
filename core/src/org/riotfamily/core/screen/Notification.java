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


public class Notification {
	
	private Object[] args;
	
	private String title;
	
	private String message;
	
	private String icon;
		
	public String getTitle() {
		return title;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getIcon() {
		return icon;
	}

	public Notification setIcon(String icon) {
		this.icon = icon;
		return this;
	}
		
	public Notification setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public Notification setMessage(String message) {
		this.message = message;
		return this;
	}
	
	public Notification setArgs(Object... args) {
		this.args = args;
		return this;
	}
	
}

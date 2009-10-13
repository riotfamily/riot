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
package org.riotfamily.core.screen.list.command;


public class CommandInfo {

	private String action;
	
	private String label;
	
	private String icon;

	private boolean showOnForm;
	
	public CommandInfo(String action, String label, String icon,
			boolean showOnForm) {
		
		this.action = action;
		this.label = label;
		this.icon = icon;
		this.showOnForm = showOnForm;
	}

	public String getAction() {
		return action;
	}
	
	public String getLabel() {
		return label;
	}

	public String getIcon() {
		return icon;
	}

	public boolean isShowOnForm() {
		return showOnForm;
	}
	
}

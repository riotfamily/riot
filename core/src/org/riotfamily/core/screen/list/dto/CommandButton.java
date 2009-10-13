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
package org.riotfamily.core.screen.list.dto;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.annotations.RemoteProperty;
import org.riotfamily.core.screen.list.command.CommandInfo;

@DataTransferObject
public class CommandButton {

	@RemoteProperty
	private String id;
	
	@RemoteProperty
	private String label;
	
	@RemoteProperty
	private String icon;
	
	@RemoteProperty
	private boolean enabled;
	
	public CommandButton(String id, CommandInfo info) {
		this(id, info, false);
	}
	
	public CommandButton(String id, CommandInfo info, boolean enabled) {
		this.id = id;
		this.label = info.getLabel();
		this.icon = info.getIcon();
		this.enabled = enabled;
	}

	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public String getIcon() {
		return icon;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
}

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
package org.riotfamily.core.screen.list.command.result;

import org.riotfamily.core.screen.list.command.CommandResult;


/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class ConfirmResult implements CommandResult {

	public static final String ACTION = "confirm";
	
	private String commandId;
	
	private String message;

	
	public ConfirmResult(String commandId, String message) {
		this.commandId = commandId;
		this.message = message;
	}

	public String getAction() {
		return ACTION;
	}
	
	public String getCommandId() {
		return this.commandId;
	}
	
	public String getMessage() {
		return this.message;
	}
	
}

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

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.annotations.RemoteProperty;
import org.riotfamily.core.screen.list.command.CommandResult;


@DataTransferObject
public class PopupResult implements CommandResult {

	private String url;
	
	private String windowName;
	
	private String arguments;
	
	private String popupBlockerMessage;
	
	
	public PopupResult(String url) {
		this.url = url;
	}
	
	@RemoteProperty
	public String getAction() {
		return "popup";
	}
	
	@RemoteProperty
	public String getPopupBlockerMessage() {
		return this.popupBlockerMessage;
	}

	public PopupResult setPopupBlockerMessage(String popupBlockerMessage) {
		this.popupBlockerMessage = popupBlockerMessage;
		return this;
	}

	@RemoteProperty
	public String getUrl() {
		return this.url;
	}

	@RemoteProperty
	public String getWindowName() {
		return this.windowName;
	}

	public PopupResult setWindowName(String windowName) {
		this.windowName = windowName;
		return this;
	}
	
	@RemoteProperty
	public String getArguments() {
		return arguments;
	}
	
	public PopupResult setArguments(String arguments) {
		this.arguments = arguments;
		return this;
	}
		
}

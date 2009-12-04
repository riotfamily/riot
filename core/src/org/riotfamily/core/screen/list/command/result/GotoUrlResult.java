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

import javax.servlet.http.HttpServletRequest;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.annotations.RemoteMethod;
import org.riotfamily.core.screen.ScreenContext;
import org.riotfamily.core.screen.list.command.CommandResult;

@DataTransferObject
public class GotoUrlResult implements CommandResult {

	private String url;
	
	private String target = "self";
	
	private boolean replace;
	
	public GotoUrlResult(String url) {
		this.url = url;
	}
	
	public GotoUrlResult(HttpServletRequest request, String url) {
		this.url = request.getContextPath() + url;
	}
	
	public GotoUrlResult(ScreenContext context) {
		this(context.getRequest(), context.getLink().getUrl());
	}
	
	@RemoteMethod
	public String getAction() {
		return "gotoUrl";
	}

	@RemoteMethod
	public boolean isReplace() {
		return this.replace;
	}

	public void setReplace(boolean replace) {
		this.replace = replace;
	}

	@RemoteMethod
	public String getTarget() {
		return this.target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	@RemoteMethod
	public String getUrl() {
		return this.url;
	}

}

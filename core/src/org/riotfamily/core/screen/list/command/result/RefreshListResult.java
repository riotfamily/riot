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
import org.directwebremoting.annotations.RemoteMethod;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.springframework.util.ObjectUtils;

@DataTransferObject
public class RefreshListResult implements CommandResult {

	private String objectId;
	
	private boolean refreshAll;
	
	public RefreshListResult() {
	}
		
	public RefreshListResult(String objectId) {
		this.objectId = objectId;
	}

	@RemoteMethod
	public String getAction() {
		return "refreshList";
	}
	
	@RemoteMethod
	public String getObjectId() {
		return objectId;
	}
	
	public RefreshListResult refreshAll() {
		this.refreshAll = true;
		return this;
	}
	
	@RemoteMethod
	public boolean isRefreshAll() {
		return objectId == null || refreshAll;
	}
	
	public int hashCode() {
		return objectId != null ? objectId.hashCode() : 0;
	}
	
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof RefreshListResult) {
			RefreshListResult other = (RefreshListResult) obj;
			return ObjectUtils.nullSafeEquals(objectId, other.objectId);
		}
		return false;
	}

}

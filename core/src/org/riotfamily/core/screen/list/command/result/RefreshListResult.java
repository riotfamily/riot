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

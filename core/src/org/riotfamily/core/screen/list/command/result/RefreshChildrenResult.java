package org.riotfamily.core.screen.list.command.result;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.annotations.RemoteMethod;
import org.springframework.util.ObjectUtils;

@DataTransferObject
public class RefreshChildrenResult implements CommandResult {

	private String objectId;
	
	public RefreshChildrenResult() {
	}
		
	public RefreshChildrenResult(String objectId) {
		this.objectId = objectId;
	}

	@RemoteMethod
	public String getAction() {
		return "refreshChildren";
	}
	
	@RemoteMethod
	public String getObjectId() {
		return objectId;
	}
	
	public int hashCode() {
		return objectId != null ? objectId.hashCode() : 0;
	}
	
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof RefreshChildrenResult) {
			RefreshChildrenResult other = (RefreshChildrenResult) obj;
			return ObjectUtils.nullSafeEquals(objectId, other.objectId);
		}
		return false;
	}

}

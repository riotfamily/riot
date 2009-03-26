package org.riotfamily.core.command.result;

import org.riotfamily.core.command.CommandContext;
import org.riotfamily.core.command.CommandResult;
import org.springframework.util.ObjectUtils;

public class RefreshSiblingsResult implements CommandResult {

public static final String ACTION = "refreshSiblings";
	
	private String objectId;
	
	public String getAction() {
		return ACTION;
	}

	public RefreshSiblingsResult() {
	}
	
	public RefreshSiblingsResult(CommandContext context) {
		this.objectId = null; //FIXME context.getObjectId();
	}
	
	public RefreshSiblingsResult(String objectId) {
		this.objectId = objectId;
	}

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
		if (obj instanceof RefreshSiblingsResult) {
			RefreshSiblingsResult other = (RefreshSiblingsResult) obj;
			return ObjectUtils.nullSafeEquals(objectId, other.objectId);
		}
		return false;
	}

}

package org.riotfamily.core.screen.list.command.result;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.annotations.RemoteProperty;
import org.riotfamily.core.screen.list.command.CommandResult;

@DataTransferObject
public class UpdateCommandsResult implements CommandResult {

	@RemoteProperty
	public String getAction() {
		return "updateCommands";
	}

}

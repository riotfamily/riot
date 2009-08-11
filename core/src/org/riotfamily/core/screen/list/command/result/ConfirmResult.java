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

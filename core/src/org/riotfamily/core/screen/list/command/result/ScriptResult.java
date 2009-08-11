package org.riotfamily.core.screen.list.command.result;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.annotations.RemoteProperty;
import org.riotfamily.core.screen.list.command.CommandResult;

@DataTransferObject
public class ScriptResult implements CommandResult {

	private String script;

    public ScriptResult(String script) {
            this.script = script;
    }
    
    @RemoteProperty
    public String getAction() {
		return "eval";
	}

    @RemoteProperty
	public String getScript() {
		return this.script;
	}

}

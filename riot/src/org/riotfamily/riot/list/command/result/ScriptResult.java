package org.riotfamily.riot.list.command.result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.riot.list.command.CommandResult;

public class ScriptResult implements CommandResult {

	private String script;

    public ScriptResult(String script) {
            this.script = script;
    }

    public String getJavaScriptCode(HttpServletRequest request, 
			HttpServletResponse response) {
    	
            return script;
    }

}

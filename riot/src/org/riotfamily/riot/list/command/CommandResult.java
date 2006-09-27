package org.riotfamily.riot.list.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Result returned by commands. Please don't create your own subclasses,
 * since the client/server communication will most likely be changed in
 * upcoming versions. Currently the JavaScript is created serverside and
 * evaluated by the client. In future versions an XML document containing
 * command tags like &lt;goto-url /&gt; or &lt;reload /&gt; will be send
 * to the client. 
 * 
 * If you want to execute custom code use a 
 * @link org.riotfamily.riot.list.command.result.ScriptResult
 */
public interface CommandResult {

	public String getJavaScriptCode(HttpServletRequest request, 
			HttpServletResponse response);

}

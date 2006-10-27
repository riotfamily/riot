/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
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

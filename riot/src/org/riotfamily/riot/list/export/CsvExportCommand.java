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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Jan-Frederic Linde [jfl at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.list.export;

import java.util.List;

import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.result.GotoUrlResult;
import org.riotfamily.riot.list.command.support.AbstractCommand;
import org.riotfamily.riot.runtime.RiotRuntime;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class CsvExportCommand extends AbstractCommand implements 
		ApplicationContextAware {
	
	private List properties;
	
	private String encoding;
	
	private RiotRuntime runtime;
	
	public void setApplicationContext(ApplicationContext context) {
		runtime = (RiotRuntime) BeanFactoryUtils.beanOfTypeIncludingAncestors(
				context, RiotRuntime.class);
	}
		
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
	public String getEncoding() {
		return this.encoding;
	}
	
	public void setProperties(List properties) {
		this.properties = properties;
	}
	
	public List getProperties() {
		return this.properties;
	}
	
	public CommandResult execute(CommandContext context) {
		StringBuffer url = new StringBuffer();
		url.append(runtime.getServletPrefix())
			.append("/csv?commandId=")
		   	.append(getId())
		    .append("&listId=")
		    .append(context.getListConfig().getId())
		    .append("&parentId")
		    .append(context.getParentId());
		return new GotoUrlResult(context, url.toString());
	}

}

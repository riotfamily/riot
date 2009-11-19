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
package org.riotfamily.core.screen.list.command.impl.export;

import java.util.List;
import java.util.Map;

import org.riotfamily.common.util.Generics;
import org.riotfamily.common.web.mvc.mapping.HandlerUrlUtils;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.impl.support.AbstractCommand;
import org.riotfamily.core.screen.list.command.result.GotoUrlResult;

public class ExportCommand extends AbstractCommand {
	
	@Override
	protected String getIcon(String action) {
		return "table_go";
	}

	private Exporter exporter;
	
	private List<String> properties;
	
	public Exporter getExporter() {
		return exporter;
	}

	public void setExporter(Exporter exporter) {
		this.exporter = exporter;
	}

	public void setProperties(List<String> properties) {
		this.properties = properties;
	}

	public List<String> getProperties() {
		return this.properties;
	}
	
	public CommandResult execute(CommandContext context, Selection selection) {
		Map<String, String> attributes = Generics.newHashMap();
		attributes.put("screenId", context.getScreen().getId());
		attributes.put("commandId", context.getCommandId());
		String url = HandlerUrlUtils.getContextRelativeUrl(context.getRequest(), 
				"exportController", attributes);
		
		return new GotoUrlResult(context.getRequest(), url);
	}
}

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
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.list.command.export;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.Generics;
import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.riot.list.ColumnConfig;
import org.riotfamily.riot.list.ListConfig;
import org.riotfamily.riot.list.support.ListParamsImpl;
import org.riotfamily.riot.list.ui.ListSession;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class ExportController implements Controller {

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String key = ServletUtils.getRequiredStringAttribute(request, "listSessionKey");
		ListSession listSession = ListSession.getListSession(request, key);
		ListConfig listConfig = listSession.getListDefinition().getListConfig();
		
		String commandId = ServletUtils.getRequiredStringAttribute(request, "commandId");
		ExportCommand command = (ExportCommand) listSession.getCommand(commandId);
		
		List<String> properties = command.getProperties();
		if (properties == null) {
			properties = Generics.newArrayList();
			for (ColumnConfig column : listConfig.getColumnConfigs()) {
				properties.add(column.getProperty());
			}
		}

		Exporter exporter = command.getExporter();
		
		String prefix = FormatUtils.toFilename(listSession.getTitle());
		String suffix = exporter.getFileExtension();
		String fileName = String.format("%s-%tF.%s", prefix, new Date(), suffix);
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
		
		ListParamsImpl params = new ListParamsImpl(listSession.getParams());
		Object parent = listSession.loadParent();
		Collection<?> items = listConfig.getDao().list(parent, params);
		exporter.export(items, parent, properties, response);
		return null;
	}
}

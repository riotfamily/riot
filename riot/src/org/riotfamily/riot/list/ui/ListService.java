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
package org.riotfamily.riot.list.ui;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.riot.list.command.CommandResult;

/**
 * @author Felix Gnass <fgnass@neteye.de>
 * @since 6.4
 */
public interface ListService {

	public ListTable getTable(String editorId, String parentId,
			HttpServletRequest request);
	
	public List getItems(String editorId, String parentId, 
			HttpServletRequest request);
	
	public List gotoPage(String editorId, String parentId, int page, 
			HttpServletRequest request);
	
	public ListTable sort(String editorId, String parentId, String property, 
			HttpServletRequest request);
	
	public List filter(String editorId, String parentId, Map filter, 
			HttpServletRequest request);
		
	public CommandResult execCommand(String editorId, String parentId, 
			ListItem item, String commandId, boolean confirmed, 
			HttpServletRequest request, HttpServletResponse response);
	
	public String getFilterForm(String editorId, String parentId,
			HttpServletRequest request);
	
}

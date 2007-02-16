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
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.list.ui;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.riot.list.command.CommandResult;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public interface ListService {

	public ListSession getOrCreateListSession(String editorId, String parentId, 
			String choose, HttpServletRequest request);
	
	public ListModel getModel(String key, HttpServletRequest request)
			throws ListSessionExpiredException;
	
	public ListModel gotoPage(String key, int page,	HttpServletRequest request)
			throws ListSessionExpiredException;
	
	public ListModel sort(String key, String property, 
			HttpServletRequest request) throws ListSessionExpiredException;
	
	public ListModel filter(String key, Map filter, HttpServletRequest request)
			throws ListSessionExpiredException;
		
	public List getFormCommands(String key, String objectId, 
			HttpServletRequest request) throws ListSessionExpiredException;
	
	public List getListCommands(String key,	HttpServletRequest request)
			throws ListSessionExpiredException;
	
	public CommandResult execCommand(String key, ListItem item, 
			String commandId, boolean confirmed, 
			HttpServletRequest request, HttpServletResponse response)
			throws ListSessionExpiredException;
	
	public String getFilterFormHtml(String key,	HttpServletRequest request)
			throws ListSessionExpiredException;
	
}

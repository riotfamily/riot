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
package org.riotfamily.riot.list.command;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.riot.dao.ListParams;
import org.riotfamily.riot.dao.RiotDao;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.list.ListConfig;

/**
 * Context passed to commands during command execution.
 */
public interface CommandContext {

	public ListDefinition getParentListDefinition();
	
	public ListDefinition getListDefinition();
	
	public ListConfig getListConfig();

	public RiotDao getDao();
	
	public ListParams getParams();

	public int getRowIndex();

	public String getParentEditorId();
	
	public String getParentId();

	public String getObjectId();

	public Object getBean();

	public Object getParent();

	public MessageResolver getMessageResolver();

	public HttpServletRequest getRequest();

	public int getItemsTotal();
	
	public int getBatchSize();

	public int getBatchIndex();
	
	public String getListSessionKey();
	
	public String getListUrl();

}
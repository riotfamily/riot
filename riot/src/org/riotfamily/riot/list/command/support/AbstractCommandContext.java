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
package org.riotfamily.riot.list.command.support;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.riot.dao.RiotDao;
import org.riotfamily.riot.editor.DisplayDefinition;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.list.ListConfig;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.ui.ListContext;
import org.riotfamily.riot.list.ui.MutableListParams;

/**
 *
 */
public abstract class AbstractCommandContext implements CommandContext {

	private ListContext listContext;
	
	public AbstractCommandContext(ListContext context) {
		this.listContext = context;
	}
	
	public ListContext getListContext() {
		return listContext;
	}

	public ListDefinition getListDefinition() {
		return listContext.getListDefinition();
	}

	public ListConfig getListConfig() {
		return listContext.getListConfig();
	}
	
	public RiotDao getDao() {
		return getListConfig().getDao();
	}

	public MutableListParams getParams() {
		return listContext.getParams();
	}
	
	public HttpServletRequest getRequest() {
		return listContext.getRequest();
	}
	
	public String getParentId() {
		return getParams().getParentId();
	}

	public DisplayDefinition getEditorDefinition() {
		return getListDefinition().getDisplayDefinition();
	}

	public Class getBeanClass() {
		return getDao().getEntityClass();
	}
	
	public MessageResolver getMessageResolver() {
		return listContext.getMessageResolver();
	}

}

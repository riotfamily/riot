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

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.riot.dao.ListParams;
import org.riotfamily.riot.dao.RiotDao;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.list.ListConfig;
import org.riotfamily.riot.list.command.CommandContext;

public class CommandContextImpl implements CommandContext {

	private ListSession session;

	private int itemsTotal;

	private int batchSize;

	private int batchIndex;
	
	private int rowIndex;

	private String objectId;
	
	private Object bean;

	private String parentEditorId;
	
	private String parentId;
	
	private Object parent;

	private HttpServletRequest request;

	public CommandContextImpl(ListSession session, HttpServletRequest request) {
		this.session = session;
		this.request = request;
	}

	public Class<?> getBeanClass() {
		return session.getBeanClass();
	}

	public RiotDao getDao() {
		return getListDefinition().getDao();
	}
	
	public Object getBean() {
		if (bean == null && objectId != null) {
			bean = getListDefinition().getDao().load(objectId);
		}
		return bean;
	}

	public void setBean(Object bean, String objectId) {
		this.bean = bean;
		this.objectId = objectId;
	}

	public Object getParent() {
		if (parent == null) {
			if (parentId != null) {
				parent = getParentListDefinition().getDao().load(parentId);
			}
		}
		return parent;
	}

	public void setParent(Object parent, String parentId, String parentEditorId) {
		this.parent = parent;
		this.parentId = parentId;
		this.parentEditorId = parentEditorId;
	}
	
	public String getParentEditorId() {
		return parentEditorId;
	}
	
	public int getItemsTotal() {
		return itemsTotal;
	}

	public void setItemsTotal(int itemsTotal) {
		this.itemsTotal = itemsTotal;
	}

	public int getBatchSize() {
		return batchSize;
	}
	
	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}
	
	public int getBatchIndex() {
		return batchIndex;
	}

	public void setBatchIndex(int batchIndex) {
		this.batchIndex = batchIndex;
	} 
	
	public ListDefinition getListDefinition() {
		return session.getListDefinition();
	}
	
	public ListDefinition getParentListDefinition() {
		return session.getParentListDefinition(parentEditorId);
	}

	public ListConfig getListConfig() {
		return getListDefinition().getListConfig();
	}

	public MessageResolver getMessageResolver() {
		return session.getMessageResolver();
	}

	public String getObjectId() {
		return objectId;
	}

	public ListParams getParams() {
		return session.getParams();
	}

	public String getParentId() {
		return parentId;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public int getRowIndex() {
		return rowIndex;
	}
	
	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}
	
	public String getListSessionKey() {
		return session.getKey();
	}
	
	public String getListUrl() {
		return getListDefinition().getEditorUrl(getObjectId(), getParentId(), getParentEditorId());
	}

}
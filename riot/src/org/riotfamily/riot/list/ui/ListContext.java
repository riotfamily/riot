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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.i18n.AdvancedMessageCodesResolver;
import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.list.ColumnConfig;
import org.riotfamily.riot.list.ListConfig;
import org.riotfamily.riot.list.command.Command;
import org.springframework.context.MessageSource;
import org.springframework.util.Assert;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Context used by ListControllers to render a list.
 */
public class ListContext {

	private ListDefinition listDefinition;
	
	private ListConfig listConfig;
	
	private MutableListParams params;
		
	private HttpServletRequest request;
	
	private HttpServletResponse response;
	
	private MessageResolver messageResolver;

	private Command command;
	
	private String objectId;
	
	private Integer orderBy;
	
	private int rowIndex;
	
	private Integer page;
	

	public ListContext(HttpServletRequest request, 
			HttpServletResponse response, MessageSource messageSource, 
			AdvancedMessageCodesResolver messageKeyResolver, Command command) 
			throws ServletRequestBindingException {
	
		this.request = request;
		this.response = response;
		this.command = command;
		
		this.messageResolver = new MessageResolver(messageSource, 
				messageKeyResolver, RequestContextUtils.getLocale(request));
		
		objectId = request.getParameter(Constants.PARAM_OBJECT_ID);
		
		rowIndex = ServletRequestUtils.getIntParameter(
				request, Constants.PARAM_ROW_INDEX, 0);
		
		orderBy = ServletRequestUtils.getIntParameter(
				request, Constants.PARAM_ORDER_BY);
		
		page = ServletRequestUtils.getIntParameter(
				request, Constants.PARAM_PAGE);
	}

	public ListConfig getListConfig() {
		return listConfig;
	}

	public void setListConfig(ListConfig listConfig) {
		this.listConfig = listConfig;
	}

	public ListDefinition getListDefinition() {
		return listDefinition;
	}

	public void setListDefinition(ListDefinition listDefinition) {
		this.listDefinition = listDefinition;
	}

	public MutableListParams getParams() {
		return params;
	}

	public void setParams(MutableListParams params) {
		this.params = params;
		
		Assert.notNull(listConfig, "listConfig must be set before " +
				"setParams() is called.");
		
		if (page != null) {
			params.setOffset((page.intValue() - 1) * params.getPageSize());
		}
		if (orderBy != null) {
			ColumnConfig col = listConfig.getColumnConfig(orderBy.intValue());
			Assert.isTrue(col.isSortable(), "Column must be sortable");
			params.orderBy(col.getProperty(), col.isAscending(), col.isCaseSensitive());
		}
	}

	public HttpServletRequest getRequest() {
		return request;
	}
	
	public HttpServletResponse getResponse() {
		return response;
	}
	
	public MessageResolver getMessageResolver() {
		return messageResolver;
	}

	public Command getCommand() {
		return command;
	}

	public String getObjectId() {
		return objectId;
	}

	public Integer getOrderBy() {
		return orderBy;
	}

	public int getRowIndex() {
		return rowIndex;
	}

}

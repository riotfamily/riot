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
package org.riotfamily.riot.form.command;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.FormContext;
import org.riotfamily.riot.dao.ListParams;
import org.riotfamily.riot.dao.RiotDao;
import org.riotfamily.riot.editor.AbstractDisplayDefinition;
import org.riotfamily.riot.editor.DisplayDefinition;
import org.riotfamily.riot.editor.EditorDefinitionUtils;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.form.ui.FormUtils;
import org.riotfamily.riot.list.ColumnConfig;
import org.riotfamily.riot.list.ListConfig;
import org.riotfamily.riot.list.command.Command;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.support.ListParamsImpl;
import org.springframework.util.Assert;
import org.springframework.web.bind.ServletRequestUtils;

public class FormCommandContext implements CommandContext {

	private ListConfig listConfig;
	
	private DisplayDefinition definition;
	
	private Object item;
	
	private Form form;
	
	private Command command;
	
	private boolean confirmed;
	
	private MessageResolver messageResolver;
	
	private Locale locale;
	
	private HttpServletRequest request;
	
	private HttpServletResponse response;
	
	public FormCommandContext(DisplayDefinition formDefinition, 
			Form form, ListConfig listConfig, Object item,
			HttpServletRequest request, HttpServletResponse response) {

		this.definition = formDefinition;
		this.form = form;
		this.item = item;
		this.listConfig = listConfig;
		this.confirmed = ServletRequestUtils.getBooleanParameter(
				request, "confirmed", false);
		
		FormContext formContext = form.getFormContext();
		this.messageResolver = formContext.getMessageResolver();
		this.locale = formContext.getLocale();
		
		this.request = request;
		this.response = response;
	}
	
	public FormCommandContext(AbstractDisplayDefinition formDefinition, 
			MessageResolver messageResolver, Locale locale, ListConfig listConfig, 
			Object item, HttpServletRequest request, HttpServletResponse response) {

		this.definition = formDefinition;
		
		this.item = item;
		this.listConfig = listConfig;
		this.confirmed = ServletRequestUtils.getBooleanParameter(
				request, "confirmed", false);
				
		this.messageResolver = messageResolver;
		this.locale = locale;
		
		this.request = request;
		this.response = response;
	}

	public int getItemsTotal() {
		return 1;
	}

	public Object getValue() {
		return null;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	public Command getCommand() {
		return command;
	}
	
	public String getProperty() {
		return null;
	}

	public ListDefinition getListDefinition() {
		return EditorDefinitionUtils.getParentListDefinition(definition);
	}

	public ListConfig getListConfig() {
		return listConfig;
	}
	
	public RiotDao getDao() {
		return listConfig.getDao();
	}

	public ListParams getParams() {
		ListParamsImpl params = new ListParamsImpl();
		params.setParentId(getParentId());
		return params;
	}

	public int getRowIndex() {
		return 0;
	}

	public DisplayDefinition getEditorDefinition() {
		return definition;
	}

	public Class getBeanClass() {
		return definition.getBeanClass();
	}

	public String getParentId() {
		if (item != null) {
			return EditorDefinitionUtils.getParentId(definition, item);
		}
		return FormUtils.getParentId(form);
	}

	public String getObjectId() {
		if (item == null) {
			return null;
		}
		return EditorDefinitionUtils.getObjectId(definition, item);
	}

	public Object getBean() {
		return item;
	}
	
	public void setItem(Object item) {
		Assert.isNull(this.item);
		this.item = item;
	}
	
	public boolean isConfirmed() {
		return this.confirmed;
	}

	public ColumnConfig getColumnConfig() {
		return null;
	}

	public MessageResolver getMessageResolver() {
		return this.messageResolver;
	}

	public Locale getLocale() {
		return this.locale;
	}

	public String getContextPath() {
		return request.getContextPath();
	}
	
	public String encodeURL(String url) {
		return response.encodeURL(url);
	}

	public HttpServletRequest getRequest() {
		return this.request;
	}
	
	public void addRowStyle(String className) {
		throw new UnsupportedOperationException();
	}
}


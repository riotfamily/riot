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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.beans.ProtectedBeanWrapper;
import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.PropertyUtils;
import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.FormRepository;
import org.riotfamily.forms.controller.FormContextFactory;
import org.riotfamily.forms.support.SimpleFormRequest;
import org.riotfamily.riot.editor.EditorDefinitionUtils;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.list.ColumnConfig;
import org.riotfamily.riot.list.ListConfig;
import org.riotfamily.riot.list.command.Command;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.result.ConfirmResult;
import org.riotfamily.riot.list.support.ListParamsImpl;
import org.riotfamily.riot.list.ui.render.RenderContext;

/**
 * @author Felix Gnass <fgnass@neteye.de>
 * @since 6.4
 */
public class ListSession implements RenderContext {

	private final String COMMAND_MESSAGE_PREFIX = "command.";
	
	private ListDefinition listDefinition;
	
	private String parentId;
	
	private MessageResolver messageResolver;
	
	private String contextPath;
	
	private Form filterForm;
	
	private ListConfig listConfig;
	
	private ListParamsImpl params;

	
	public ListSession(ListDefinition listDefinition, String parentId,
			MessageResolver messageResolver, String contextPath,
			FormRepository formRepository, 
			FormContextFactory formContextFactory) {
		
		this.listDefinition = listDefinition;
		this.parentId = parentId;
		this.messageResolver = messageResolver;
		this.contextPath = contextPath;
		this.listConfig = listDefinition.getListConfig();
		
		params = new ListParamsImpl();
		params.setParentId(parentId);
		
		String formId = listConfig.getFilterFormId();
		if (formId != null) {
			filterForm = formRepository.createForm(formId);
			filterForm.setFormContext(formContextFactory.createFormContext(
					messageResolver, contextPath, null));
			
			filterForm.setTemplate(ResourceUtils.getPath(getClass(), "FilterForm.ftl"));
			params.setFilteredProperties(filterForm.getEditorBinder()
					.getBoundProperties());
		}
		
		params.setPageSize(listConfig.getPageSize());
		params.setOrder(listConfig.getDefaultOrder());
		
	}

	public List getItems(HttpServletRequest request) {
		Object parent = EditorDefinitionUtils.loadParent(
				listDefinition, params.getParentId());

		Collection beans = listConfig.getDao().list(parent, params);
		ArrayList items = new ArrayList(beans.size());
		int rowIndex = 0;
		Iterator it = beans.iterator();
		while (it.hasNext()) {
			Object bean = it.next();
			ListItem item = new ListItem();
			item.setRowIndex(rowIndex++);
			item.setObjectId(EditorDefinitionUtils.getObjectId(listDefinition, bean));
			item.setColumns(getColumns(bean));
			item.setCommands(getCommandStates(listConfig.getColumnCommands(), 
					item, bean, request));
			
			items.add(item);
		}
		return items;
	}
	
	private List getColumns(Object bean) {
		ArrayList result = new ArrayList();
		Iterator it = listConfig.getColumnConfigs().iterator();
		ProtectedBeanWrapper wrapper = new ProtectedBeanWrapper(bean);
		while (it.hasNext()) {
			ColumnConfig col = (ColumnConfig) it.next();
			String propertyName = col.getProperty();
			Object value = wrapper.getPropertyValue(propertyName);
			StringWriter writer = new StringWriter();
			col.getRenderer().render(propertyName, value, this, 
					new PrintWriter(writer));
			
			result.add(writer.toString());
		}
		return result;
	}
	
	public ListTable getTable(HttpServletRequest request) {
		ListTable table = new ListTable();

		table.setEditorId(listDefinition.getId());
		table.setParentId(parentId);
		table.setItemCommandCount(listConfig.getColumnCommands().size());
		table.setListCommands(getListCommands(request));
		
		ArrayList columns = new ArrayList();
		Iterator it = listConfig.getColumnConfigs().iterator();
		while (it.hasNext()) {
			ColumnConfig config = (ColumnConfig) it.next();
			ListColumn column = new ListColumn();
			column.setProperty(config.getProperty());
			column.setHeading(getHeading(config.getProperty(), 
					config.getLookupLevel()));
			
			column.setSortable(config.isSortable());
			if (params.hasOrder() && params.getPrimaryOrder()
					.getProperty().equals(config.getProperty())) {
				
				column.setSorted(true);
				column.setAscending(params.getPrimaryOrder().isAscending());
			}
			columns.add(column);
		}
		table.setColumns(columns);
		table.setRows(getItems(request));
		return table;
	}
	
	private String getHeading(String property, int lookupLevel) {
		Class clazz = getBeanClass();
		String root = property;
        int pos = property.indexOf('.');
        if (pos > 0) {
            root = property.substring(0, pos);
        }
        if (lookupLevel > 1) {
        	clazz = PropertyUtils.getPropertyType(clazz, root);
        	String nestedProperty = property.substring(pos + 1);
        	return getHeading(nestedProperty, lookupLevel - 1);
        }
	    return messageResolver.getPropertyLabel(
	    		getListId(), clazz, property);
	}
	
	public ListTable sort(String property, HttpServletRequest request) {
		ColumnConfig col = listConfig.getColumnConfig(property);
		params.orderBy(property, col.isAscending(), col.isCaseSensitive());
		return getTable(request);
	}
	
	public List search(String search, HttpServletRequest request) {
		params.setSearch(search);
		return getItems(request);
	}
	
	public String getFilterForm() {
		if (filterForm != null) {
			StringWriter writer = new StringWriter();
			filterForm.render(new PrintWriter(writer));
			return writer.toString();
		}
		return null;
	}

	public List filter(Map filter, HttpServletRequest request) {
		filterForm.processRequest(new SimpleFormRequest(filter));
		params.setFilter(filterForm.populateBackingObject());
		return getItems(request);
	}
	
	public List gotoPage(int page, HttpServletRequest request) {
		params.setPage(page);
		return getItems(request);
	}
	
	public List getListCommands(HttpServletRequest request) {
		return getCommandStates(listConfig.getCommands(), null, null, request);
	}
		
	public List getFormCommands(String objectId, HttpServletRequest request) {
		Object bean = null;
		if (objectId != null) {
			bean = listConfig.getDao().load(objectId);
		}
		return getCommandStates(listConfig.getFormCommands(), 
				null, bean, request);
	}
	
	private List getCommandStates(List commands, ListItem item, Object bean, 
			HttpServletRequest request) {
		
		ArrayList result = new ArrayList();
		CommandContextImpl context = new CommandContextImpl(this, request);
		context.setBean(bean);
		context.setItem(item);
		Iterator it = commands.iterator();
		while (it.hasNext()) {
			Command command = (Command) it.next();
			CommandState state = new CommandState();
			String action = command.getAction(context);
			state.setId(command.getId());
			state.setAction(action);
			state.setEnabled(command.isEnabled(context));
			state.setLabel(messageResolver.getMessage(
					COMMAND_MESSAGE_PREFIX + action, null,
					FormatUtils.camelToTitleCase(action)));
			
			result.add(state);
		}
		return result;
	}
		
	public CommandResult execCommand(ListItem item, String commandId, 
			boolean confirmed, HttpServletRequest request, 
			HttpServletResponse response) {
		
		Command command = item != null 
				? listConfig.getColumnCommand(commandId)
				: listConfig.getListCommand(commandId);
		
		CommandContextImpl context = new CommandContextImpl(this, request);
		context.setItem(item);
		
		if (!confirmed) {
			String message = command.getConfirmationMessage(context);
			if (message != null) {
				return new ConfirmResult(item, commandId, message);
			}
		}
		return command.execute(context);
	}
	
	public Object loadBean(String objectId) {
		return listDefinition.getListConfig().getDao().load(objectId);
	}
	
	public ListDefinition getListDefinition() {
		return listDefinition;
	}

	public String getListId() {
		return listDefinition.getListId();
	}
	
	public ListParamsImpl getParams() {
		return this.params;
	}

	public String getParentId() {
		return this.parentId;
	}

	public Class getBeanClass() {
		return listDefinition.getBeanClass();
	}
		
	public MessageResolver getMessageResolver() {
		return messageResolver;
	}
	
	public String getContextPath() {
		return contextPath;
	}
	
}

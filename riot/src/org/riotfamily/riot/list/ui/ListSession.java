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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.beans.ProtectedBeanWrapper;
import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.common.util.PropertyUtils;
import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.FormRepository;
import org.riotfamily.forms.controller.FormContextFactory;
import org.riotfamily.forms.element.core.TextField;
import org.riotfamily.forms.support.SimpleFormRequest;
import org.riotfamily.riot.editor.DisplayDefinition;
import org.riotfamily.riot.editor.EditorDefinitionUtils;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.editor.TreeDefinition;
import org.riotfamily.riot.list.ColumnConfig;
import org.riotfamily.riot.list.ListConfig;
import org.riotfamily.riot.list.command.Command;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.core.ChooseCommand;
import org.riotfamily.riot.list.command.core.DescendCommand;
import org.riotfamily.riot.list.command.result.ConfirmResult;
import org.riotfamily.riot.list.support.ListParamsImpl;
import org.riotfamily.riot.list.ui.render.RenderContext;
import org.riotfamily.riot.security.AccessController;

/**
 * @author Felix Gnass <fgnass@neteye.de>
 * @since 6.4
 */
public class ListSession implements RenderContext {

	private String key;
	
	private ListDefinition listDefinition;
	
	private String parentId;
	
	private MessageResolver messageResolver;
	
	private String contextPath;
	
	private Form filterForm;
	
	private TextField searchField;
	
	private String filterFormHtml;
	
	private String title;
	
	private ListConfig listConfig;
	
	private List listCommands;
	
	private List itemCommands;
	
	private String[] defaultCommandIds;
	
	private ListParamsImpl params;

	
	public ListSession(String key, ListDefinition listDefinition, 
			String parentId, MessageResolver messageResolver, String contextPath,
			FormRepository formRepository, 
			FormContextFactory formContextFactory) {
		
		this.key = key;
		this.listDefinition = listDefinition;
		this.parentId = parentId;
		this.messageResolver = messageResolver;
		this.contextPath = contextPath;
		this.listConfig = listDefinition.getListConfig();
		
		listCommands = listConfig.getCommands();
		itemCommands = listConfig.getColumnCommands();
		defaultCommandIds = listConfig.getDefaultCommandIds();
		title = listDefinition.createReference(parentId, messageResolver).getLabel();
		params = new ListParamsImpl();
		
		String formId = listConfig.getFilterFormId();
		if (formId != null) {
			filterForm = formRepository.createForm(formId);
		}
		
		if (listConfig.getSearchProperties() != null) {
			if (filterForm == null) {
				filterForm = new Form();
				filterForm.setBeanClass(HashMap.class);
			}
			searchField = new TextField();
			searchField.setLabel("Search");
			filterForm.addElement(searchField);
		}
		
		if (filterForm != null) {
			filterForm.setFormContext(formContextFactory.createFormContext(
					messageResolver, contextPath, null));
			
			filterForm.setTemplate(ResourceUtils.getPath(getClass(), "FilterForm.ftl"));
			params.setFilteredProperties(filterForm.getEditorBinder()
					.getBoundProperties());
			
			params.setFilter(filterForm.populateBackingObject());
			updateFilterFormHtml();
		}
		
		params.setSearchProperties(listConfig.getSearchProperties());
		params.setPageSize(listConfig.getPageSize());
		params.setOrder(listConfig.getDefaultOrder());
		
	}

	public String getKey() {
		return key;
	}
	
	public void setChooserTarget(DisplayDefinition target) {
		ListDefinition targetList = EditorDefinitionUtils
				.getParentListDefinition(target);
		
		ListDefinition nextList = targetList;
		if (listDefinition != targetList) {
			nextList = EditorDefinitionUtils.getNextListDefinition(
					listDefinition, targetList);
		}
		if (nextList instanceof TreeDefinition) {
			TreeDefinition tree = (TreeDefinition) nextList;
			nextList = tree.getNodeListDefinition();
		}
		
		listCommands = Collections.EMPTY_LIST;
		itemCommands = new ArrayList();
		itemCommands.add(new DescendCommand(nextList, target));
		
		if (listDefinition.getBeanClass().isAssignableFrom(
				target.getBeanClass())) {
			
			itemCommands.add(new ChooseCommand(target));
		}
		
		defaultCommandIds = new String[] { DescendCommand.ID, ChooseCommand.ID };
	}
	
	public String getTitle() {
		return title;
	}
	
	private void updateFilterFormHtml() {
		StringWriter writer = new StringWriter();
		filterForm.render(new PrintWriter(writer));
		filterFormHtml = writer.toString();
	}
		
	public ListModel getItems(HttpServletRequest request) {
		Object parent = EditorDefinitionUtils.loadParent(
				listDefinition, parentId);

		int itemsTotal = listConfig.getDao().getListSize(parent, params);
		Collection beans = listConfig.getDao().list(parent, params);
		if (itemsTotal < beans.size()) {
			itemsTotal = beans.size();
		}
		
		ListModel model = new ListModel(itemsTotal, params.getPageSize(), 
				params.getPage());

		ArrayList items = new ArrayList(beans.size());
		int rowIndex = 0;
		Iterator it = beans.iterator();
		while (it.hasNext()) {
			Object bean = it.next();
			ListItem item = new ListItem();
			item.setRowIndex(rowIndex++);
			item.setLastOnPage(!it.hasNext());
			item.setObjectId(EditorDefinitionUtils.getObjectId(listDefinition, bean));
			item.setColumns(getColumns(bean));
			item.setCommands(getCommandStates(itemCommands, 
					item, bean, itemsTotal, request));
			
			item.setDefaultCommandIds(defaultCommandIds);
			items.add(item);
		}
		model.setItems(items);
		return model;
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
	
	public ListModel getModel(HttpServletRequest request) {
		ListModel model = getItems(request);

		model.setEditorId(listDefinition.getId());
		model.setParentId(parentId);
		model.setItemCommandCount(itemCommands.size());
		model.setListCommands(getListCommands(request));
		
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
		model.setColumns(columns);
		model.setFilterFormHtml(filterFormHtml);
		return model;
	}
		
	private String getHeading(String property, int lookupLevel) {
		Class clazz = getBeanClass();
		if (clazz != null) {
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
		}
	    return messageResolver.getPropertyLabel(
	    		getListId(), clazz, property);
	}
	
	public ListModel sort(String property, HttpServletRequest request) {
		ColumnConfig col = listConfig.getColumnConfig(property);
		params.orderBy(property, col.isAscending(), col.isCaseSensitive());
		return getModel(request);
	}
	
	public String[] getSearchProperties() {
		return listConfig.getSearchProperties();
	}
	
	public String getSearchQuery() {
		return params.getSearch();
	}
	
	public String getFilterFormHtml() {
		return filterFormHtml;
	}

	public ListModel filter(Map filter, HttpServletRequest request) {
		if (filterForm != null) {
			filterForm.processRequest(new SimpleFormRequest(filter));
			params.setFilter(filterForm.populateBackingObject());
			if (searchField != null) {
				params.setSearch(searchField.getText());
			}
			updateFilterFormHtml();
		}
		params.setPage(1);
		ListModel result = getItems(request);
		result.setFilterFormHtml(filterFormHtml);
		return result;
	}
	
	public ListModel gotoPage(int page, HttpServletRequest request) {
		params.setPage(page);
		return getItems(request);
	}
	
	public boolean hasListCommands() {
		return !listCommands.isEmpty();
	}
	
	public List getListCommands(HttpServletRequest request) {
		//REVISIT: Should we pass the correct item count here? 
		return getCommandStates(listCommands, null, null, -1, request);
	}
		
	public List getFormCommands(String objectId, HttpServletRequest request) {
		Object bean = null;
		if (objectId != null) {
			bean = listConfig.getDao().load(objectId);
		}
		return getCommandStates(listConfig.getFormCommands(), 
				new ListItem(objectId), bean, 1, request);
	}
	
	private List getCommandStates(List commands, ListItem item, Object bean, 
			int itemsTotal, HttpServletRequest request) {
		
		ArrayList result = new ArrayList();
		CommandContextImpl context = new CommandContextImpl(this, request);
		context.setBean(bean);
		context.setItem(item);
		context.setItemsTotal(itemsTotal);
		Iterator it = commands.iterator();
		while (it.hasNext()) {
			Command command = (Command) it.next();
			CommandState state = new CommandState();
			String action = command.getAction(context);
			boolean granted = AccessController.isGranted(
					action, bean, listDefinition);
			
			state.setId(command.getId());
			state.setAction(action);
			state.setEnabled(granted && command.isEnabled(context));
			state.setLabel(command.getLabel(context));
			result.add(state);
		}
		return result;
	}
	
	public CommandResult execCommand(ListItem item, String commandId, 
			boolean confirmed, HttpServletRequest request, 
			HttpServletResponse response) {
		
		Collection commands = item != null ? itemCommands : listCommands;
		Command command = getCommand(commands, commandId);
		Object bean = null;
		CommandContextImpl context = new CommandContextImpl(this, request);
		if (item != null) {
			context.setItem(item);
		}
		else {
			bean = EditorDefinitionUtils.loadParent(listDefinition, parentId);
			context.setBean(bean);
		}
		String action = command.getAction(context);
		if (AccessController.isGranted(action, bean, listDefinition)) {
			if (!confirmed) {
				String message = command.getConfirmationMessage(context);
				if (message != null) {
					return new ConfirmResult(item, commandId, message);
				}
			}
			return command.execute(context);
		}
		else {
			return null;
		}
	}
	
	private Command getCommand(Collection commands, String id) {
		Iterator it = commands.iterator();
		while (it.hasNext()) {
			Command command = (Command) it.next();
			if (id.equals(command.getId())) {
				return command;
			}
		}
		throw new IllegalArgumentException("No such command: " + id);
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
		return params;
	}

	public String getParentId() {
		return parentId;
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

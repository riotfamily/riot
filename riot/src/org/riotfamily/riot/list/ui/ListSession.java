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

import org.riotfamily.common.beans.PropertyUtils;
import org.riotfamily.common.beans.ProtectedBeanWrapper;
import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.Generics;
import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.common.web.ui.RenderContext;
import org.riotfamily.forms.Element;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.controller.FormContextFactory;
import org.riotfamily.forms.element.TextField;
import org.riotfamily.forms.factory.FormRepository;
import org.riotfamily.forms.request.SimpleFormRequest;
import org.riotfamily.riot.dao.ParentChildDao;
import org.riotfamily.riot.dao.RiotDao;
import org.riotfamily.riot.dao.SortableDao;
import org.riotfamily.riot.dao.TreeHintDao;
import org.riotfamily.riot.editor.EditorDefinition;
import org.riotfamily.riot.editor.EditorDefinitionUtils;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.editor.TreeDefinition;
import org.riotfamily.riot.list.ColumnConfig;
import org.riotfamily.riot.list.ListConfig;
import org.riotfamily.riot.list.command.BatchCommand;
import org.riotfamily.riot.list.command.Command;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.CommandState;
import org.riotfamily.riot.list.command.core.ChooseCommand;
import org.riotfamily.riot.list.command.core.DescendCommand;
import org.riotfamily.riot.list.command.result.ConfirmBatchResult;
import org.riotfamily.riot.list.command.result.ConfirmResult;
import org.riotfamily.riot.list.support.ListParamsImpl;
import org.riotfamily.riot.security.AccessController;
import org.springframework.beans.NullValueInNestedPathException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
	* @author Felix Gnass [fgnass at neteye dot de]
	* @since 6.4
	*/
public class ListSession implements RenderContext {

	private static final DefaultTransactionDefinition TRANSACTION_DEFINITION =
			new DefaultTransactionDefinition(
			TransactionDefinition.PROPAGATION_REQUIRED);

	private String key;

	private ListDefinition listDefinition;
	
	private TreeDefinition treeDefinition;
	
	private TreeHintDao treeHintDao;

	private String parentId;
	
	private String parentEditorId;

	private MessageResolver messageResolver;

	private String contextPath;

	private PlatformTransactionManager transactionManager;

	private Form filterForm;

	private TextField searchField;

	private String filterFormHtml;

	private String title;

	private ListConfig listConfig;

	private boolean sortable;
	
	private List<Command> listCommands;

	private List<Command> itemCommands;

	private String[] defaultCommandIds;

	private ListParamsImpl params;

	private boolean expired;

	public ListSession(String key, ListDefinition listDefinition,
			String parentId, String parentEditorId,
			MessageResolver messageResolver, String contextPath,
			FormRepository formRepository,
			FormContextFactory formContextFactory,
			PlatformTransactionManager transactionManager) {

		this.key = key;
		this.listDefinition = listDefinition;
		this.parentId = parentId;
		this.messageResolver = messageResolver;
		this.contextPath = contextPath;
		this.transactionManager = transactionManager;
		this.listConfig = listDefinition.getListConfig();
		this.sortable = listConfig.getDao() instanceof SortableDao;
		
		if (parentEditorId == null) {
			EditorDefinition parentList = EditorDefinitionUtils.getParentListDefinition(listDefinition);
			if (parentList != null) {
				this.parentEditorId =  parentList.getId();
			}
		}
		else {
			this.parentEditorId = parentEditorId;
		}
		
		if (listDefinition instanceof TreeDefinition) {
			treeDefinition = (TreeDefinition) listDefinition;
			RiotDao dao = treeDefinition.getListConfig().getDao();
			if (dao instanceof TreeHintDao) {
				treeHintDao = (TreeHintDao) dao;
			}
		}
		
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

			Iterator<Element> it = filterForm.getRegisteredElements().iterator();
			while (it.hasNext()) {
				Element e = (Element) it.next();
				e.setRequired(false);
			}

			params.setFilteredProperties(filterForm.getEditorBinder()
					.getBoundProperties());

			params.setFilter(filterForm.populateBackingObject());
			updateFilterFormHtml();
		}

		params.setSearchProperties(listConfig.getSearchProperties());
		params.setPageSize(listConfig.getPageSize());
		params.setOrder(listConfig.getDefaultOrder());
	}
	
	private Map<String,String> getTexts() {
		HashMap<String,String> texts = Generics.newHashMap();
		addText(texts, "label.tree.selectTarget");
		addText(texts, "label.tree.cancelCommand");
		addText(texts, "label.selection.clear");
		addText(texts, "label.selection.count");
		return texts;
	}
	
	private void addText(Map<String,String> map, String key) {
		map.put(key, messageResolver.getMessage(key, key));
	}

	public String getKey() {
		return key;
	}

	public void setChooserTarget(EditorDefinition target) {
		listCommands = Collections.emptyList();
		itemCommands = Generics.newArrayList();

		ListDefinition targetList = null;
		if (target instanceof ListDefinition) {
			targetList = (ListDefinition) target;
		}
		else {
			targetList = EditorDefinitionUtils.getParentListDefinition(target);
		}

		if (targetList != listDefinition) {
			ListDefinition nextList = targetList;
			if (listDefinition != targetList) {
				nextList = EditorDefinitionUtils.getNextListDefinition(
						listDefinition, targetList);
			}
			itemCommands.add(new DescendCommand(nextList, target));
		}

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
	
	public ListModel getChildren(String parentId, HttpServletRequest request) {
		return getChildren(loadBean(parentId), request);
	}
	
	private ListModel getChildren(Object parent, HttpServletRequest request) {
		ListModel model = getItems(parent, getObjectId(parent), 
				this.listDefinition.getId(), loadParent(), request);
		
		CommandContextImpl context = new CommandContextImpl(this, request);
		context.setParent(parent, getObjectId(parent), listDefinition.getId());
		context.setItemsTotal(model.getItemsTotal());
		return model;
	}
	
	private ListModel getItems(Object parent, String parentId, 
			String parentEditorId, Object root, HttpServletRequest request) {
		
		int itemsTotal = listConfig.getDao().getListSize(parent, params);
		int pageSize = params.getPageSize();
		Collection<?> beans = listConfig.getDao().list(parent, params);
		if (itemsTotal < beans.size()) {
			itemsTotal = beans.size();
			pageSize = itemsTotal;
		}
		ListModel model = new ListModel(itemsTotal, pageSize, params.getPage());
		model.setParentId(parentId);
		model.setParentEditorId(parentEditorId);
		fillInItems(model, beans, root, request);
		return model;
	}

	/**
	 * Sets a List of ListItem objects on the given model.
	 */
	private void fillInItems(ListModel model, Collection<?> beans, Object root,
			HttpServletRequest request) {
		
		ArrayList<ListItem> items = new ArrayList<ListItem>(beans.size());
		int rowIndex = 0;
		Iterator<?> it = beans.iterator();
		while (it.hasNext()) {
			Object bean = it.next();
			ListItem item = new ListItem();
			item.setRowIndex(rowIndex++);
			item.setObjectId(EditorDefinitionUtils.getObjectId(listDefinition, bean));
			item.setParentId(model.getParentId());
			item.setParentEditorId(model.getParentEditorId());
			item.setColumns(getColumns(bean));
			item.setCommands(getCommandStates(itemCommands,
					item, bean, model.getItemsTotal(), request));

			if (listConfig.getRowStyleProperty() != null) {
				item.setCssClass(FormatUtils.toCssClass(
						PropertyUtils.getPropertyAsString(bean, 
						listConfig.getRowStyleProperty())));
			}
			item.setDefaultCommandIds(defaultCommandIds);
			item.setExpandable(isExpandable(bean, root));
			items.add(item);
		}
		model.setItems(items);
	}
	
	private boolean isExpandable(Object bean, Object root) {
		if (treeHintDao != null) {
			return treeHintDao.hasChildren(bean, root, params);
		}
		if (treeDefinition != null) {
			int listSize = listConfig.getDao().getListSize(bean, params);
			if (listSize == -1) {
				listSize = listConfig.getDao().list(bean, params).size();
			}
			return listSize > 0;
		}
		return false;
	}
	
	/**
	 * Returns a List of HTML markup for each column.
	 */
	private List<String> getColumns(Object bean) {
		ArrayList<String> result = Generics.newArrayList();
		Iterator<ColumnConfig> it = listConfig.getColumnConfigs().iterator();
		ProtectedBeanWrapper wrapper = new ProtectedBeanWrapper(bean);
		while (it.hasNext()) {
			ColumnConfig col = (ColumnConfig) it.next();
			String propertyName = col.getProperty();
			Object value = null;
			if (propertyName != null) {
				try {
					value = wrapper.getPropertyValue(propertyName);
				}
				catch (NullValueInNestedPathException ex) {
				}
			}
			else {
				value = bean;
			}
			StringWriter writer = new StringWriter();
			col.getRenderer().render(value, this, new PrintWriter(writer));
			result.add(writer.toString());
		}
		return result;
	}

	public ListModel getModel(String expandedId, HttpServletRequest request) {
		ListModel model;
		Object parent = loadParent();
		if (expandedId != null) {
			model = buildTree(loadBean(expandedId), null, request);
		}
		else {
			model = getItems(parent, this.parentId, this.parentEditorId, parent, request);
		}
		model.setEditorId(listDefinition.getId());
		model.setItemCommandCount(itemCommands.size());
		
		CommandContextImpl context = new CommandContextImpl(this, request);
		context.setParent(parent, this.parentId, this.parentEditorId);
		context.setItemsTotal(model.getItemsTotal());
		model.setListCommands(getListCommandStates(listCommands, context, request));
		model.setBatchCommands(getBatchStates(itemCommands, context, request));
		model.setTexts(getTexts());
		model.setCssClass(listConfig.getId());
		model.setTree(treeDefinition != null);
		fillInColumnConfigs(model);
		model.setFilterFormHtml(filterFormHtml);
		return model;
	}

	private ListModel buildTree(Object expanded, ListModel subTree, HttpServletRequest request) {
		ListModel children;
		if (treeDefinition.isNode(expanded)) {
			children = getChildren(expanded, request);
			if (subTree != null) {
				ListItem item = children.findItem(subTree.getParentId());
				item.setChildren(subTree);
			}
			
			ParentChildDao dao = (ParentChildDao) listConfig.getDao();
			Object parent = dao.getParent(expanded);
			
			return buildTree(parent, children, request);
		}
		else {
			Object root = loadParent();
			children = getItems(root, this.parentId, this.parentEditorId, root, request);
			if (subTree != null) {
				ListItem item = children.findItem(subTree.getParentId());
				if (item != null) {
					item.setChildren(subTree);
				}
			}
			return children;
		}
	}
	
	private void fillInColumnConfigs(ListModel model) {
		ArrayList<ListColumn> columns = Generics.newArrayList();
		Iterator<ColumnConfig> it = listConfig.getColumnConfigs().iterator();
		while (it.hasNext()) {
			ColumnConfig config = (ColumnConfig) it.next();
			ListColumn column = new ListColumn();
			column.setProperty(config.getProperty());
			column.setHeading(getHeading(config.getProperty(),
					config.getLookupLevel()));

			column.setSortable(sortable && config.isSortable());
			column.setCssClass(FormatUtils.toCssClass(config.getProperty()));
			if (params.hasOrder() && params.getPrimaryOrder()
					.getProperty().equals(config.getProperty())) {

				column.setSorted(true);
				column.setAscending(params.getPrimaryOrder().isAscending());
			}
			columns.add(column);
		}
		model.setColumns(columns);
	}
	
	private String getHeading(String property, int lookupLevel) {
		return getHeading(getBeanClass(), property, lookupLevel);
	}

	private String getHeading(Class<?> clazz, String property, int lookupLevel) {
		if (property == null) {
			return null;
		}
		if (clazz != null) {
			String root = property;
			int pos = property.indexOf('.');
			if (pos > 0) {
				root = property.substring(0, pos);
			}
			if (lookupLevel > 1) {
				clazz = PropertyUtils.getPropertyType(clazz, root);
				if (pos > 0) {
					String nestedProperty = property.substring(pos + 1);
					return getHeading(clazz, nestedProperty, lookupLevel - 1);
				}
				else {
					return messageResolver.getClassLabel(null, clazz);
				}
			}
		}
		return messageResolver.getPropertyLabel(
				getListId(), clazz, property);
	}

	public ListModel sort(String property, HttpServletRequest request) {
		ColumnConfig col = listConfig.getColumnConfig(property);
		params.orderBy(property, col.isAscending(), col.isCaseSensitive());
		return getModel(null, request);
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

	public ListModel filter(Map<String,Object> filter, HttpServletRequest request) {
		if (filterForm != null) {
			filterForm.processRequest(new SimpleFormRequest(filter));
			params.setFilter(filterForm.populateBackingObject());
			if (searchField != null) {
				params.setSearch(searchField.getText());
			}
			updateFilterFormHtml();
		}
		params.setPage(1);
		Object root = loadParent();
		ListModel result = getItems(root, this.parentId, this.parentEditorId, root, request);
		result.setFilterFormHtml(filterFormHtml);
		return result;
	}

	public ListModel gotoPage(int page, HttpServletRequest request) {
		params.setPage(page);
		Object root = loadParent();
		return getItems(root, this.parentId, this.parentEditorId, root, request);
	}

	public boolean hasListCommands() {
		return !listCommands.isEmpty();
	}

	public List<?> getFormCommands(String objectId, HttpServletRequest request) {
		Object bean = null;
		if (objectId != null) {
			bean = loadBean(objectId);
		}
		return getCommandStates(listConfig.getFormCommands(),
				new ListItem(objectId), bean, 1, request);
	}

	public CommandState getParentCommandState(String commandId, ListItem item,
			HttpServletRequest request) {

		Command command = getCommand(listCommands, commandId);
		CommandContextImpl context = new CommandContextImpl(this, request);
		Object parent;
		if (item != null) {
			parent = loadBean(item.getObjectId());
			context.setParent(parent, item.getObjectId(), listDefinition.getId());
			context.setRowIndex(item.getRowIndex());
		}
		else {
			parent = loadParent();
			context.setParent(parent, this.parentId, this.parentEditorId);
		}
		
		CommandState state = command.getState(context);
		boolean granted = AccessController.isGranted(
				state.getAction(), parent);

		state.setEnabled(state.isEnabled() && granted);
		return state;
	}
	
	private List<CommandState> getCommandStates(List<Command> commands, ListItem item, Object bean,
			int itemsTotal, HttpServletRequest request) {

		ArrayList<CommandState> states = Generics.newArrayList();
		CommandContextImpl context = new CommandContextImpl(this, request);
		context.setBean(bean, item.getObjectId());
		context.setParent(null, item.getParentId(), item.getParentEditorId());
		context.setItemsTotal(itemsTotal);
		context.setRowIndex(item.getRowIndex());
		Iterator<Command> it = commands.iterator();
		while (it.hasNext()) {
			Command command = it.next();
			states.add(command.getState(context));
		}
		checkPermissions(states, bean);
		return states;
	}
	
	private List<CommandState> getListCommandStates(List<Command> commands, CommandContext context,
			HttpServletRequest request) {

		ArrayList<CommandState> states = Generics.newArrayList();
		Iterator<Command> it = commands.iterator();
		while (it.hasNext()) {
			Command command = it.next();
			CommandState state = command.getState(context);
			states.add(state);
		}
		checkPermissions(states, context.getParent());
		return states;
	}
	
	private List<CommandState> getBatchStates(List<?> commands, CommandContext context, 
			HttpServletRequest request) {

		ArrayList<CommandState> states = Generics.newArrayList();
		Iterator<?> it = commands.iterator();
		while (it.hasNext()) {
			Object command = it.next();
			if (command instanceof BatchCommand) {
				BatchCommand bc = (BatchCommand) command;
				states.addAll(bc.getBatchStates(context));
			}
		}
		checkPermissions(states, context.getParent());
		return states;
	}
	
	private void checkPermissions(List<CommandState> states, Object bean) {
		Iterator<CommandState> it = states.iterator();
		while (it.hasNext()) {
			CommandState state = it.next();
			boolean granted = AccessController.isGranted(
					state.getAction(), bean);

			state.setEnabled(state.isEnabled() && granted);
		}
	}

	public CommandResult execListCommand(String parentId, 
			CommandState commandState, boolean confirmed,
			HttpServletRequest request, HttpServletResponse response) {
		
		Command command = getCommand(listCommands, commandState.getId());
		CommandContextImpl context = new CommandContextImpl(this, request);
		Object target = null;
		if (parentId != null) {
			target = loadBean(parentId);
			context.setParent(target, parentId, listDefinition.getId());
		}
		else {
			target = loadParent();
			context.setParent(target, this.parentId, this.parentEditorId);
		}
		return execCommand(null, command, context, target, 
				commandState, confirmed, request, response);
	}
	
	public CommandResult execItemCommand(ListItem item, 
			CommandState commandState, boolean confirmed,
			HttpServletRequest request, HttpServletResponse response) {
		
		Command command = getCommand(itemCommands, commandState.getId());
		CommandContextImpl context = new CommandContextImpl(this, request);
		Object target = loadBean(item.getObjectId());
		context.setBean(target, item.getObjectId());
		context.setParent(null, item.getParentId(), item.getParentEditorId());
		context.setRowIndex(item.getRowIndex());
		return execCommand(item, command, context, target, 
				commandState, confirmed, request, response);
	}
	
	private CommandResult execCommand(ListItem item, Command command, 
			CommandContext context, Object target, 
			CommandState commandState, boolean confirmed,
			HttpServletRequest request, HttpServletResponse response) {
		
		String action = command.getState(context).getAction();
		if (AccessController.isGranted(action, target)) {
			if (!confirmed) {
				String message = command.getConfirmationMessage(context);
				if (message != null) {
					return new ConfirmResult(item, commandState, message);
				}
			}
			TransactionStatus status = transactionManager.getTransaction(TRANSACTION_DEFINITION);
			CommandResult result;
			try {
				result = command.execute(context); 
			}
			catch (RuntimeException e) {
				transactionManager.rollback(status);
				throw e;
			}
			transactionManager.commit(status);
			return result; 
		}
		else {
			return null;
		}
	}
	
	public CommandResult execBatchCommand(List<ListItem> items, CommandState commandState,
			boolean confirmed, HttpServletRequest request,
			HttpServletResponse response) {

		BatchCommand command = (BatchCommand) getCommand(itemCommands, commandState.getId());
		CommandContextImpl context = new CommandContextImpl(this, request);
		context.setBatchSize(items.size());
		if (!confirmed) {
			String message = command.getBatchConfirmationMessage(context, commandState.getAction());
			if (message != null) {
				return new ConfirmBatchResult(commandState, message);
			}
		}
		TransactionStatus status = transactionManager.getTransaction(TRANSACTION_DEFINITION);
		CommandResult result = null;
		try {
			Iterator<ListItem> it = items.iterator();
			int batchIndex = 0;
			while (it.hasNext()) {
				ListItem item = it.next();
				Object bean = loadBean(item.getObjectId());
				context.setBean(bean, item.getObjectId());
				context.setParent(null, item.getParentId(), item.getParentEditorId());
				context.setRowIndex(item.getRowIndex());
				if (AccessController.isGranted(commandState.getAction(), bean)) {
					context.setBatchIndex(batchIndex);
					result = command.execute(context);
				}
				batchIndex++;
			}
		}
		catch (RuntimeException e) {
			transactionManager.rollback(status);
			throw e;
		}
		transactionManager.commit(status);
		return result; 
	}
	
	private Command getCommand(Collection<Command> commands, String id) {
		Iterator<Command> it = commands.iterator();
		while (it.hasNext()) {
			Command command = it.next();
			if (id.equals(command.getId())) {
				return command;
			}
		}
		throw new IllegalArgumentException("No such command: " + id);
	}

	private Object loadBean(String objectId) {
		return listConfig.getDao().load(objectId);
	}
	
	private String getObjectId(Object bean) {
		return listConfig.getDao().getObjectId(bean);
	}
	
	private Object loadParent(String parentId, String parentEditorId) {
		ListDefinition listDef = getParentListDefinition(parentEditorId);
		if (listDef != null) {
			return listDef.getDao().load(parentId);	
		}
		return null;
	}
	
	private Object loadParent() {
		return loadParent(this.parentId, this.parentEditorId);
	}

	public ListDefinition getListDefinition() {
		return listDefinition;
	}
	
	public ListDefinition getParentListDefinition(String parentEditorId) {
		if (listDefinition.getId().equals(parentEditorId)) {
			return listDefinition;
		}
		return EditorDefinitionUtils.getParentListDefinition(listDefinition);
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

	public Class<?> getBeanClass() {
		return listDefinition.getBeanClass();
	}

	public MessageResolver getMessageResolver() {
		return messageResolver;
	}

	public String getContextPath() {
		return contextPath;
	}

	void invalidate() {
		expired = true;
	}

	public boolean isExpired() {
		return expired;
	}

}

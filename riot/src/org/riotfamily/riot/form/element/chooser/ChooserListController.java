package org.riotfamily.riot.form.element.chooser;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.forms.FormRepository;
import org.riotfamily.riot.editor.EditorDefinition;
import org.riotfamily.riot.editor.EditorDefinitionUtils;
import org.riotfamily.riot.editor.EditorRepository;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.editor.TreeDefinition;
import org.riotfamily.riot.list.ColumnConfig;
import org.riotfamily.riot.list.ListRepository;
import org.riotfamily.riot.list.command.Command;
import org.riotfamily.riot.list.command.support.CommandExecutor;
import org.riotfamily.riot.list.ui.AbstractListController;
import org.riotfamily.riot.list.ui.ListContext;
import org.riotfamily.riot.list.ui.ViewModel;
import org.riotfamily.riot.list.ui.render.CellRenderer;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.util.Assert;

public class ChooserListController extends AbstractListController
		implements ChooserController {

	public static final String CONTROLLER_ATTR = 
			ChooserListController.class.getName();
	
	public static final String TARGET_EDITOR_ATTR = 
			ChooserListController.class.getName() + ".targetEditor";
		
	public static final String NEXT_LIST_ATTR = 
			ChooserListController.class.getName() + ".nextList";
	
	private String targetEditorIdAttribute = "targetEditorId";
	
	private Command descendCommand = new DescendCommand();
	
	private Command chooseCommand = new ChooseCommand();
	
	private ColumnConfig[] columns;
	
	public ChooserListController(EditorRepository editorRepository, 
			ListRepository listRepository, FormRepository formRepository,
			PlatformTransactionManager transactionManager,
			CommandExecutor commandExecutor) {
		
		super(editorRepository, listRepository, formRepository, 
				transactionManager, commandExecutor);
		
		CellRenderer renderer = listRepository.getItemCommandRenderer();
		CellRenderer headingRenderer = listRepository.getDefaultHeadingRenderer();
		columns = new ColumnConfig[] { 
			new ColumnConfig(chooseCommand, renderer, headingRenderer),
			new ColumnConfig(descendCommand, renderer, headingRenderer)
		};
	}
	
	public String getUrl(String targetEditorId) {
		return getUrl(targetEditorId, null, null);
	}
	
	public String getUrl(String targetEditorId, String editorId, String parentId) {
		HashMap attrs = new HashMap();
		attrs.put(targetEditorIdAttribute, targetEditorId);
		if (editorId != null) {
			attrs.put(getEditorIdAttribute(), editorId);
		}
		if (parentId != null) {
			attrs.put(getParentIdAttribute(), parentId);
		}
		return getUrl(attrs);
	}
	
	protected Command getCommand(String commandId) {
		if (commandId != null) {
			if (commandId.equals(descendCommand.getId())) {
				return descendCommand;
			}
			if (commandId.equals(chooseCommand.getId())) {
				return chooseCommand;
			}
		}
		return null;
	}
	
	public void setTargetEditorIdAttribute(String targetEditorIdAttribute) {
		this.targetEditorIdAttribute = targetEditorIdAttribute;
	}
	
	protected ListDefinition getListDefinition(ListContext context) {
		HttpServletRequest request = context.getRequest();
		request.setAttribute(CONTROLLER_ATTR, this);
		
		String targetEditorId = (String) request.getAttribute(targetEditorIdAttribute);
		EditorDefinition target = getEditorRepository().getEditorDefinition(targetEditorId);
		request.setAttribute(TARGET_EDITOR_ATTR, target);
			
		ListDefinition targetList = EditorDefinitionUtils.getListDefinition(target);
		
		ListDefinition list = null;
		
		String editorId = (String) request.getAttribute(getEditorIdAttribute());
		if (editorId != null) {
			list = getEditorRepository().getListDefinition(editorId);
			Assert.notNull(list, "No such editor: " + editorId);
		}
		else {
			list = getRootList(target);
		}
		
		ListDefinition nextList = targetList;
		if (list != targetList) {
			nextList = getNextList(list, targetList);
		}
		if (nextList instanceof TreeDefinition) {
			TreeDefinition tree = (TreeDefinition) nextList;
			nextList = tree.getNodeListDefinition();
		}
		request.setAttribute(NEXT_LIST_ATTR, nextList);
		
		return list;
	}

	protected ViewModel createViewModel(final ListContext context) {
		return (ViewModel) execInTransaction(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus ts) {
				ChooserViewModelBuilder viewModelBuilder = 
				new ChooserViewModelBuilder(context, columns);
				//REVISIT viewModelBuilder.setDefaultCommand(descend);
				return viewModelBuilder.buildModel();
			}
		});
	}
	
	private static ListDefinition getRootList(EditorDefinition def) {
		ListDefinition list = EditorDefinitionUtils.getListDefinition(def);
		ListDefinition parent = EditorDefinitionUtils.getParentListDefinition(list);
		while (parent != null) {
			list = parent;
			parent = EditorDefinitionUtils.getParentListDefinition(list);
		}
		return list;
	}
	
	private static ListDefinition getNextList(
			ListDefinition source, ListDefinition target) {
		
		ListDefinition def = target;
		ListDefinition parent = EditorDefinitionUtils.getParentListDefinition(def); 
		while (parent != source && parent != null) {
			def = parent;
			parent = EditorDefinitionUtils.getParentListDefinition(def);
		}
		return def;
	}

}

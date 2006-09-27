package org.riotfamily.riot.editor;

import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.riot.editor.ui.EditorReference;
import org.riotfamily.riot.list.ListConfig;
import org.riotfamily.riot.list.ListRepository;
import org.springframework.util.Assert;

/**
 *
 */
public class ListDefinition extends AbstractEditorDefinition {

	protected static final String TYPE_LIST = "list";

	private DisplayDefinition displayDefinition;

	private DisplayDefinition parentDisplayDefinition;

	private String listId;

	private ListConfig listConfig;
	
	public ListDefinition(EditorRepository repository) {
		this(repository, TYPE_LIST);
	}
	
	protected ListDefinition(EditorRepository repository, String editorType) {
		super(repository, editorType);
	}
	
	public ListDefinition(ListDefinition prototype, 
			EditorRepository repository) {
		
		this(repository);
		displayDefinition = prototype.getDisplayDefinition();
		parentDisplayDefinition = prototype.getParentDisplayDefinition();
		listId = prototype.getListId();
		listConfig = prototype.getListConfig();
		setId(prototype.getId());
	}
	
	public String getListId() {
		return listId;
	}
	
	protected String getDefaultName() {
		return getListId();
	}

	public void setListId(String listId) {
		this.listId = listId;
		ListRepository repository = getEditorRepository().getListRepository();
		listConfig = repository.getListConfig(listId);
		Assert.notNull(listConfig, "No such list: " + listId);
	}

	public Class getBeanClass() {
		return listConfig.getItemClass();
	}

	public DisplayDefinition getDisplayDefinition() {
		return displayDefinition;
	}

	public void setDisplayDefinition(DisplayDefinition editorDef) {
		this.displayDefinition = editorDef;
	}

	public DisplayDefinition getParentDisplayDefinition() {
		return parentDisplayDefinition;
	}

	public void setParentEditorDefinition(EditorDefinition parentDef) {
		super.setParentEditorDefinition(parentDef);
		if (parentDef instanceof DisplayDefinition) {
			this.parentDisplayDefinition = (DisplayDefinition) parentDef;
		}
	}

	public EditorReference createEditorPath(String objectId, String parentId,
			MessageResolver messageResolver) {

		EditorReference parent = null;
		if (getParentEditorDefinition() != null) {
			// Delegate call to parent editor passing the parentId as objectId
			parent = getParentEditorDefinition().createEditorPath(
					parentId, null, messageResolver);
		}
		
		EditorReference component = createReference(parentId, messageResolver);
		
		component.setParent(parent);
		return component;
	}

	public EditorReference createEditorPath(Object bean,
			MessageResolver messageResolver) {

		EditorReference component = null;
		EditorReference parent = null;
		
		if (parentDisplayDefinition != null) {
			parent = parentDisplayDefinition.createEditorPath(bean, messageResolver);
			component = createReference(parent.getObjectId(), messageResolver);
			component.setParent(parent);
		}
		else { 
			component = createReference(null, messageResolver);
			if (getParentEditorDefinition() != null) {
				parent = getParentEditorDefinition().createEditorPath(
						null, null, messageResolver);
				
				component.setParent(parent);
			}
		}
		return component;
	}
	
	/**
	 * Creates a reference to the list. The method is used by the {@link 
	 * org.riotfamily.riot.form.ui.FormController FormController} to create
	 * links pointing to the child lists.
	 */
	public EditorReference createReference(String parentId,
			MessageResolver messageResolver) {
		
		EditorReference ref = new EditorReference();
		ref.setEditorType(getEditorType());
		ref.setIcon(getIcon());
		
		String defaultLabel = FormatUtils.camelToTitleCase(getListId());
		ref.setLabel(messageResolver.getMessage(
				getMessageKey().toString(), null, defaultLabel));
		
		ref.setDescription(messageResolver.getMessage(
				getMessageKey().append(".description").toString(), null, null));
		
		ref.setEditorUrl(getEditorUrl(null, parentId));
		return ref;
	}
	
	public ListConfig getListConfig() {
		return listConfig;
	}
	
}

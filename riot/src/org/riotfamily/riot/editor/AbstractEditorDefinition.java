package org.riotfamily.riot.editor;

import java.util.List;

import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.riot.editor.ui.EditorReference;

public abstract class AbstractEditorDefinition implements EditorDefinition {

	private String id;
	
	private String name;
	
	private EditorRepository editorRepository;
	
	private EditorDefinition parentEditorDefinition;
	
	private String icon;
	
	private boolean hidden;
		
	private String editorType;
		
	public AbstractEditorDefinition(EditorRepository editorRepository, 
			String editorType) {
		
		this.editorRepository = editorRepository;
		this.editorType = editorType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public final void setName(String name) {
		this.name = name;
		if (id == null) {
			id = name;
		}
	}

	public final String getName() {
		return name != null ? name : getDefaultName();
	}
	
	protected String getDefaultName() {
		return null;
	}

	protected String getEditorType() {
		return this.editorType;
	}

	protected void setEditorType(String editorType) {
		this.editorType = editorType;
	}

	public Class getBeanClass() {
		return null;
	}
	
	public EditorDefinition getParentEditorDefinition() {
		return this.parentEditorDefinition;
	}

	public void setParentEditorDefinition(EditorDefinition parentEditorDefinition) {
		this.parentEditorDefinition = parentEditorDefinition;
	}
	
	public void addReference(List refs, DisplayDefinition parentDef, 
			Object parent, MessageResolver messageResolver) {
		
		String parentId = null;
		if (parent != null) {
			parentId = EditorDefinitionUtils.getObjectId(parentDef, parent);
		}
		EditorReference ref = createReference(parentId, messageResolver);
		ref.setEnabled(parent != null);
		refs.add(ref);
	}
	
	protected EditorRepository getEditorRepository() {
		return editorRepository;
	}
	
	public String getEditorUrl(String objectId, String parentId) {
		return editorRepository.getEditorUrl(this, objectId, parentId);
	}
	
	public String getIcon() {
		return this.icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public boolean isHidden() {
		return this.hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
		
	protected StringBuffer getMessageKey() {
		StringBuffer key = new StringBuffer();
		key.append(getEditorType());
		key.append('.');
		key.append(getName());
		return key;
	}

}

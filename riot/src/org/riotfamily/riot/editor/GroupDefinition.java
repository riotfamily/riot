package org.riotfamily.riot.editor;

import java.util.LinkedList;
import java.util.List;

import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.riot.editor.ui.EditorReference;

/**
 *
 */
public class GroupDefinition extends AbstractEditorDefinition {

	protected static final String TYPE_GROUP = "group";
	
	private List editorDefinitions;
	
	public GroupDefinition(EditorRepository editorRepository) {
		super(editorRepository, TYPE_GROUP);
	}
	
	public List getEditorDefinitions() {
		return editorDefinitions;
	}

	public void setEditorDefinitions(List editorDefinitions) {
		this.editorDefinitions = editorDefinitions;
	}

	public void addEditorDefinition(EditorDefinition editorDefinition) {
		if (editorDefinitions == null) {
			editorDefinitions = new LinkedList();
		}
		editorDefinitions.add(editorDefinition);
		editorDefinition.setParentEditorDefinition(this);
	}

	public EditorReference createEditorPath(String objectId, String parentId,
			MessageResolver messageResolver) {

		EditorReference parent = null;
		if (getParentEditorDefinition() != null) {
			parent = getParentEditorDefinition().createEditorPath(
					parentId, null, messageResolver);
		}
		EditorReference component = createReference(messageResolver);
		
		component.setParent(parent);
		return component;
	}

	public EditorReference createEditorPath(Object bean,
			MessageResolver messageResolver) {

		EditorReference component = null;
		EditorReference parent = null;
		if (getParentEditorDefinition() != null) {
			parent = getParentEditorDefinition().createEditorPath(
					bean, messageResolver);
			
			component = createReference(messageResolver);
			component.setParent(parent);
		}
		else {
			component = createReference(messageResolver);
		}
		return component;
	}

	public EditorReference createReference(String parentId, 
			MessageResolver messageResolver) {
		
		return createReference(messageResolver);
	}
	
	private EditorReference createReference(MessageResolver messageResolver) {
		
		EditorReference ref = new EditorReference();
		ref.setEditorType(TYPE_GROUP);
		ref.setIcon(getIcon());
		
		String defaultLabel = FormatUtils.camelToTitleCase(getId());
		ref.setLabel(messageResolver.getMessage(getMessageKey().toString(), 
				null, defaultLabel));
		
		ref.setDescription(messageResolver.getMessage(
				getMessageKey().append(".description").toString(), null, null));
		
		ref.setEditorUrl(getEditorUrl(null, null));
		return ref;
	}

}

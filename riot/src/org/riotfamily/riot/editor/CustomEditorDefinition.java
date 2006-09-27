package org.riotfamily.riot.editor;

import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.riot.editor.ui.EditorReference;

public class CustomEditorDefinition extends AbstractEditorDefinition {
	
	protected static final String TYPE_CUSTOM = "custom";
		
	private String url;
	
	private String target;
		
	
	public CustomEditorDefinition(EditorRepository editorRepository) {
		super(editorRepository, TYPE_CUSTOM);
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return this.url;
	}	

	public String isTarget() {
		return this.target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public EditorReference createEditorPath(String objectId, String parentId, 
			MessageResolver messageResolver) {
		
		EditorReference component = createReference(parentId, messageResolver);
		if (getParentEditorDefinition() != null) {
			EditorReference parent = getParentEditorDefinition()
					.createEditorPath(null, null, messageResolver);
			
			component.setParent(parent);
		}
		return component;
	}

	public EditorReference createEditorPath(Object bean, MessageResolver messageResolver) {
		return createEditorPath(null, null, messageResolver);
	}

	public EditorReference createReference(String parentId, 
			MessageResolver messageResolver) {
		
		EditorReference ref = new EditorReference();
		ref.setEditorType(getEditorType());
		ref.setIcon(getIcon());
		
		ref.setLabel(messageResolver.getMessage(
				getMessageKey().toString(), 
				null, FormatUtils.camelToTitleCase(getId())));
		
		ref.setDescription(messageResolver.getMessage(
				getMessageKey().append(".description").toString(), 
				null, null));
		
		ref.setEditorUrl(getEditorUrl(null, null));
		ref.setTargetWindow(target);
		
		return ref;
	}

	public String getEditorUrl(String objectId, String parentId) {
		if (target != null) {
			return url;
		}
		return super.getEditorUrl(objectId, parentId);
	}
	
	

}

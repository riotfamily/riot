package org.riotfamily.riot.editor.ui;

/**
 *
 */
public class EditorReference {

	private EditorReference parent;

	private Object bean;
	
	private String objectId;
	
	private String label;
	
	private String description;

	private String icon;
	
	private String editorUrl;

	private String editorType;
	
	private String targetWindow;

	private boolean enabled = true;

	public EditorReference getParent() {
		return parent;
	}

	public void setParent(EditorReference parent) {
		this.parent = parent;
	}

	public EditorReference getRoot() {
		return parent != null ? parent.getRoot() : this;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Object getBean() {
		return bean;
	}

	public void setBean(Object bean) {
		this.bean = bean;
	}

	public String getEditorUrl() {
		return editorUrl;
	}

	public void setEditorUrl(String editorUrl) {
		this.editorUrl = editorUrl;
	}	

	public String getTargetWindow() {
		return targetWindow;
	}

	public void setTargetWindow(String targetWindow) {
		this.targetWindow = targetWindow;
	}

	public String getEditorType() {
		return editorType;
	}

	public void setEditorType(String editorType) {
		this.editorType = editorType;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getIcon() {
		return this.icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	
}

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
package org.riotfamily.riot.editor.ui;

/**
 *
 */
public class EditorReference {

	private EditorReference parent;

	private Object bean;
	
	private String editorId;
	
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

	public String getEditorId() {
		return this.editorId;
	}

	public void setEditorId(String editorId) {
		this.editorId = editorId;
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

	public String toString() {
		return editorUrl;
	}
}

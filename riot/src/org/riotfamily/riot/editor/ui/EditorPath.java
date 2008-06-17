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

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.Generics;
import org.springframework.util.Assert;

/**
 *
 */
public class EditorPath {

	private LinkedList<EditorReference> components = Generics.newLinkedList();

	private String editorId;

	private String objectId;

	private String parentId;

	private EditorReference subPage;
	
	public EditorPath() {
	}
	
	public EditorPath(String editorId, String objectId, String parentId,
			EditorReference lastComponent) {

		this.editorId = editorId;
		this.objectId = objectId;
		this.parentId = parentId;

		EditorReference comp = lastComponent;
		while (comp != null) {
			addComponent(comp);
			comp = comp.getParent();
		}
	}

	public void addComponent(EditorReference reference) {
		if (components.isEmpty() || reference.getEditorUrl() == null) {
			reference.setEnabled(false);
		}
		components.addFirst(reference);
	}
	
	public void setSubPage(String title) {
		if (title != null) {
			if (subPage == null) {
				Assert.notEmpty(components);
				EditorReference lastRef = (EditorReference) components.getLast();
				lastRef.setEnabled(true);
				subPage = new EditorReference();
				subPage.setEnabled(false);
				subPage.setEditorType("custom");
				subPage.setParent(lastRef);
				components.addLast(subPage);
			}
			subPage.setLabel(title);
		}
		else if (subPage != null) {
			subPage.getParent().setEnabled(false);
			components.remove(subPage);
		}
	}

	public String getEditorId() {
		return editorId;
	}

	public String getObjectId() {
		return objectId;
	}

	public String getParentId() {
		return parentId;
	}

	public String getSubPage() {
		if (subPage == null) {
			return null;
		}
		return subPage.getLabel();
	}
	
	public List<EditorReference> getComponents() {
		return components;
	}

	public void encodeUrls(HttpServletResponse response) {
		for (EditorReference comp : components) {
			if (comp.getEditorUrl() != null) {
				String encodedUrl = response.encodeURL(comp.getEditorUrl());
				comp.setEditorUrl(encodedUrl);
			}
		}
	}
}

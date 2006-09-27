package org.riotfamily.riot.editor.ui;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.util.Assert;

/**
 *
 */
public class EditorPath {

	private LinkedList components = new LinkedList();

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
		if (components.isEmpty()) {
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
	
	public List getComponents() {
		return components;
	}

	public void encodeUrls(HttpServletResponse response) {
		Iterator it = components.iterator();
		while (it.hasNext()) {
			EditorReference comp = (EditorReference) it.next();
			if (comp.getEditorUrl() != null) {
				String encodedUrl = response.encodeURL(comp.getEditorUrl());
				comp.setEditorUrl(encodedUrl);
			}
		}
	}
}

package org.riotfamily.riot.editor.ui;

import java.util.LinkedList;
import java.util.List;

/**
 * Model that is passed to the view of the 
 * {@link org.riotfamily.riot.editor.ui.EditorGroupController}.
 */
public class EditorGroup {

	private String id;
	
	private String title;

	private List editors = new LinkedList();

	
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public void addReference(EditorReference reference) {
		editors.add(reference);
	}
	
	public List getEditors() {
		return editors;
	}
}

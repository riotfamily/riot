package org.riotfamily.riot.editor;


public class ViewDefinition extends AbstractDisplayDefinition {

	protected static final String TYPE_VIEW = "view";
	
	private String template;
	
	public ViewDefinition(EditorRepository editorRepository) {
		super(editorRepository, TYPE_VIEW);
	}

	public String getTemplate() {
		return this.template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}
	
}

package org.riotfamily.riot.editor;


/**
 *
 */
public class FormDefinition extends AbstractDisplayDefinition 
		implements Cloneable {

	protected static final String TYPE_FORM = "form";
	
	private String discriminatorValue;

	private String formId;

	
	public FormDefinition(EditorRepository editorRepository) {
		super(editorRepository, TYPE_FORM);
	}
	
	public String getFormId() {
		return formId;
	}
	
	public void setId(String id) {
		super.setId(id);
	}

	public void setFormId(String formId) {
		this.formId = formId;
		setBeanClass(getEditorRepository().getFormRepository().getBeanClass(formId));		
	}
	
	protected String getDefaultName() {
		return getFormId();
	}

	public String getDiscriminatorValue() {
		return discriminatorValue;
	}

	public void setDiscriminatorValue(String discriminatorValue) {
		this.discriminatorValue = discriminatorValue;
	}
	
	public FormDefinition copy(String idPrefix) {
		try {
			FormDefinition copy = (FormDefinition) clone();
			copy.setId(idPrefix + getId());
			getEditorRepository().addEditorDefinition(copy);
			return copy;
		}
		catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

}

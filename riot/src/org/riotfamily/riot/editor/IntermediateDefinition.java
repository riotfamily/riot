package org.riotfamily.riot.editor;

import org.riotfamily.riot.editor.ui.EditorReference;

public class IntermediateDefinition extends AbstractDisplayDefinition {

	private ListDefinition nestedListDefinition;

	public IntermediateDefinition(ListDefinition parentListDefinition,
			ListDefinition nestedListDefinition) {
		
		super(nestedListDefinition.getEditorRepository(), "none");
		setParentEditorDefinition(parentListDefinition);
		this.nestedListDefinition = nestedListDefinition;
		nestedListDefinition.setParentEditorDefinition(this);
	}

	public ListDefinition getNestedListDefinition() {
		return this.nestedListDefinition;
	}

	public EditorReference createPathComponent(Object bean, String parentId) {
		EditorReference reference = super.createPathComponent(bean, parentId);
		reference.setEnabled(false);
		return reference;
	}

}

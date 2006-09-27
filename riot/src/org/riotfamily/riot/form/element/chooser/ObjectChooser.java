package org.riotfamily.riot.form.element.chooser;

import org.riotfamily.forms.element.support.AbstractChooser;
import org.riotfamily.riot.editor.DisplayDefinition;
import org.riotfamily.riot.editor.EditorDefinition;
import org.riotfamily.riot.editor.EditorDefinitionUtils;
import org.riotfamily.riot.editor.EditorRepository;
import org.riotfamily.riot.editor.ListDefinition;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.util.Assert;

public class ObjectChooser extends AbstractChooser 
		implements BeanFactoryAware {

	private String targetEditorId;
		
	private BeanFactory beanFactory;
	
	private ChooserController chooserController;
	
	private EditorRepository editorRepository;
	
	private DisplayDefinition targetEditorDefinition;
	
	
	public void setTargetEditorId(String targetEditorId) {
		this.targetEditorId = targetEditorId;
	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}
	
	protected void afterFormSet() {
		if (chooserController == null) {
			Assert.isInstanceOf(ListableBeanFactory.class, beanFactory,
					"Not a ListableBeanFactory");
			
			ListableBeanFactory lbf = (ListableBeanFactory) beanFactory;
			chooserController = (ChooserController) 
					BeanFactoryUtils.beanOfTypeIncludingAncestors(
					lbf, ChooserController.class);
			
			Assert.notNull(chooserController, 
					"No ChooserListController found in BeanFactory");
		}
		
		log.debug("Looking up editor: " + targetEditorId);
		editorRepository = chooserController.getEditorRepository();
		EditorDefinition editor = editorRepository.getEditorDefinition(
				targetEditorId);
		
		Assert.notNull(editor, "No such EditorDefinition: " + targetEditorId);
		targetEditorDefinition = getDisplayDefinition(editor);
	}

	private DisplayDefinition getDisplayDefinition(EditorDefinition def) {
		if (def instanceof DisplayDefinition) {
			return (DisplayDefinition) def;
		}
		else if (def instanceof ListDefinition) {
			ListDefinition listDef = (ListDefinition) def;
			return listDef.getDisplayDefinition();
		}
		else {
			throw new IllegalArgumentException(
					"Neither a List- nor FormDefinition: " + def);
		}
	}
	
	protected Object loadBean(String objectId) {
		return EditorDefinitionUtils.loadBean(targetEditorDefinition, objectId);
	}
	
	protected String getDisplayName(Object object) {
		return targetEditorDefinition.getLabel(object);
	}

	protected String getChooserUrl() {
		return chooserController.getUrl(targetEditorId);
	}
	
}

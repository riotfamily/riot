package org.riotfamily.riot.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.riotfamily.common.collection.TypeDifferenceComparator;
import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.PropertyUtils;
import org.riotfamily.common.web.view.LabelValueBean;


public class FormChooserDefinition extends FormDefinition {
	
	private String discriminatorProperty;

	private List formDefinitions = new ArrayList();
	
	
	public FormChooserDefinition(EditorRepository editorRepository) {
		super(editorRepository);
	}

	public void addFormDefinition(FormDefinition formDefinition) {
		formDefinitions.add(formDefinition);
		if (formDefinition.getLabelProperty() == null) {
			formDefinition.setLabelProperty(getLabelProperty());
		}
		formDefinition.setParentEditorDefinition(getParentEditorDefinition());
	}

	public String getFormId() {
		FormDefinition defaultFormDef = (FormDefinition) formDefinitions.get(0); 
		return defaultFormDef.getFormId();
	}

	protected String getDefaultName() {
		return null;
	}
	
	protected FormDefinition getFormDefinition(String objectId) {
		Object bean = loadBean(objectId);
		if (discriminatorProperty != null) {
			return getFormDefinitionByDiscriminator(bean);
		}
		else {
			return getNearestFormDefintionByClass(bean);
		}
	}
	
	public void setParentEditorDefinition(EditorDefinition editorDef) {
		super.setParentEditorDefinition(editorDef);
		Iterator it = formDefinitions.iterator();
		while (it.hasNext()) {
			FormDefinition formDef = (FormDefinition) it.next();
			formDef.setParentEditorDefinition(editorDef);
		}
	}
	
	public void addChildEditorDefinition(EditorDefinition editorDef) {
		super.addChildEditorDefinition(editorDef);
		Iterator it = formDefinitions.iterator();
		while (it.hasNext()) {
			FormDefinition formDef = (FormDefinition) it.next();
			formDef.addChildEditorDefinition(editorDef);
		}
	}
	
	protected FormDefinition getFormDefinitionByDiscriminator(Object bean) {
		String discriminator = PropertyUtils.getPropertyAsString(
				bean, discriminatorProperty);

		Iterator it = formDefinitions.iterator();
		while (it.hasNext()) {
			FormDefinition formDefinition = (FormDefinition) it.next();
			if (formDefinition.getDiscriminatorValue().equals(discriminator)) {
				return formDefinition;
			}
		}
		return null;
	}
	
	protected FormDefinition getNearestFormDefintionByClass(Object bean) {
		TreeSet forms = new TreeSet(new FormDefinitionComparator(bean));
		forms.addAll(formDefinitions);
		return (FormDefinition) forms.first();
	}
		
	public String getEditorUrl(String objectId, String parentId) {
		if (objectId != null) {
			FormDefinition formDefinition = getFormDefinition(objectId);
			return formDefinition.getEditorUrl(objectId, parentId);
		}
		else {
			return super.getEditorUrl(null, parentId);
		}
	}
		
	public Collection createOptions(String parentId, 
			MessageResolver messageResolver) {
		
		ArrayList options = new ArrayList();
		Iterator it = formDefinitions.iterator();
		while (it.hasNext()) {
			FormDefinition option = (FormDefinition) it.next();
			String label = messageResolver.getMessage(
					option.getMessageKey().toString(), null, 
					FormatUtils.camelToTitleCase(option.getFormId()));
			
			options.add(new LabelValueBean(label, option.getFormId()));
		}
		return options;
	}
	
	public FormDefinition copy(String idPrefix) {
		FormChooserDefinition copy = (FormChooserDefinition) 
				super.copy(idPrefix);
		
		copy.formDefinitions = new ArrayList();
		Iterator it = formDefinitions.iterator();
		while (it.hasNext()) {
			FormDefinition formDefinition = (FormDefinition) it.next();
			copy.formDefinitions.add(formDefinition.copy(idPrefix));
		}
		return copy;
	}
	
	protected static class FormDefinitionComparator 
			extends TypeDifferenceComparator {
		
		public FormDefinitionComparator(Object bean) {
			super(bean.getClass());
		}
		
		public int compare(Object o1, Object o2) {
			FormDefinition fd1 = (FormDefinition) o1;
			FormDefinition fd2 = (FormDefinition) o2;
			return super.compare(fd1.getBeanClass(), fd2.getBeanClass());
		}
	}
}

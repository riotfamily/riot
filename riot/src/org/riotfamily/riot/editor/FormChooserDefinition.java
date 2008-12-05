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
package org.riotfamily.riot.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import org.riotfamily.common.beans.PropertyUtils;
import org.riotfamily.common.collection.TypeComparatorUtils;
import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.Generics;
import org.riotfamily.riot.form.ui.FormOption;


public class FormChooserDefinition extends FormDefinition {

	private String discriminatorProperty;

	private List<FormDefinition> formDefinitions = Generics.newArrayList();


	public FormChooserDefinition(EditorRepository editorRepository) {
		super(editorRepository);
	}
	
	public void setDiscriminatorProperty(String discriminatorProperty) {
		this.discriminatorProperty = discriminatorProperty;
	}

	public void addFormDefinition(FormDefinition formDef) {
		formDefinitions.add(formDef);
		formDef.setParentEditorDefinition(getParentEditorDefinition());
		if (getLabelProperty() != null && formDef.getLabelProperty() == null) {
			formDef.setLabelProperty(getLabelProperty());
		}
	}

	public String getFormId() {
		FormDefinition defaultFormDef = (FormDefinition) formDefinitions.get(0);
		return defaultFormDef.getFormId();
	}

	public Class<?> getBeanClass() {
		return getParentEditorDefinition().getBeanClass();
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
		for (FormDefinition formDef : formDefinitions) {
			formDef.setParentEditorDefinition(editorDef);
		}
	}

	public void addChildEditorDefinition(EditorDefinition editorDef) {
		super.addChildEditorDefinition(editorDef);
		for (FormDefinition formDef : formDefinitions) {
			formDef.getChildEditorDefinitions().add(editorDef);
		}
	}

	protected FormDefinition getFormDefinitionByDiscriminator(Object bean) {
		String discriminator = PropertyUtils.getPropertyAsString(
				bean, discriminatorProperty);

		for (FormDefinition formDef : formDefinitions) {
			if (formDef.getDiscriminatorValue().equals(discriminator)) {
				return formDef;
			}
		}
		return null;
	}

	protected FormDefinition getNearestFormDefintionByClass(Object bean) {
		TreeSet<FormDefinition> forms = Generics.newTreeSet(new FormDefinitionComparator(bean));
		forms.addAll(formDefinitions);
		return (FormDefinition) forms.first();
	}

	public String getEditorUrl(String objectId, String parentId, 
			String parentEditorId) {
		
		if (objectId != null) {
			FormDefinition formDefinition = getFormDefinition(objectId);
			return formDefinition.getEditorUrl(objectId, parentId, parentEditorId);
		}
		else {
			StringBuffer sb = new StringBuffer();
			sb.append(getEditorRepository().getRiotServletPrefix());
			sb.append("/form-chooser/").append(getId());
			sb.append("?form=").append(getFormId());
			if (parentId != null) {
				sb.append("&parentId=").append(parentId);
			}
			if (parentEditorId != null) {
			    sb.append("&parentEditorId=").append(parentEditorId);
			}			
			return sb.toString();
		}
	}

	public Collection<FormOption> createOptions(String parentId,
			MessageResolver messageResolver) {

		ArrayList<FormOption> options = Generics.newArrayList();
		for (FormDefinition formDef : formDefinitions) {
			String label = messageResolver.getMessage(
					formDef.getMessageKey().toString(), null,
					FormatUtils.camelToTitleCase(formDef.getFormId()));

			options.add(new FormOption(label, formDef.getFormId()));
		}
		return options;
	}

	protected static class FormDefinitionComparator
			implements Comparator<FormDefinition> {

		private Class<?> beanClass;
		
		public FormDefinitionComparator(Object bean) {
			this.beanClass = bean.getClass();
		}

		public int compare(FormDefinition fd1, FormDefinition fd2) {
			return TypeComparatorUtils.compare(fd1.getBeanClass(), 
					fd2.getBeanClass(), beanClass);
		}
	}
}

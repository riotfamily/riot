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
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.riotfamily.common.beans.PropertyUtils;
import org.riotfamily.common.collection.TypeDifferenceComparator;
import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.riot.form.ui.FormOption;


public class FormChooserDefinition extends FormDefinition {

	private String discriminatorProperty;

	private List formDefinitions = new ArrayList();


	public FormChooserDefinition(EditorRepository editorRepository) {
		super(editorRepository);
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

	public Class getBeanClass() {
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
			formDef.getChildEditorDefinitions().add(editorDef);
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
			StringBuffer sb = new StringBuffer();
			sb.append(getEditorRepository().getRiotServletPrefix());
			sb.append("/form-chooser/").append(getId());
			sb.append("?form=").append(getFormId());
			if (parentId != null) {
				sb.append("&parentId=").append(parentId);
			}
			return sb.toString();
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

			options.add(new FormOption(label, option.getFormId()));
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

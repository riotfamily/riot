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

import org.riotfamily.riot.form.ui.FormController;
import org.springframework.util.Assert;


/**
 *
 */
public class FormDefinition extends AbstractObjectEditorDefinition
		implements FormReference, Cloneable {

	protected static final String TYPE_FORM = "form";

	private String discriminatorValue;

	private String formId;


	public FormDefinition(EditorRepository editorRepository) {
		setEditorRepository(editorRepository);
	}

	public String getEditorType() {
		return TYPE_FORM;
	}
	
	public String getFormId() {
		return formId;
	}

	public void setId(String id) {
		super.setId(id);
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public Class<?> getBeanClass() {
		Assert.notNull(formId, "A formId must be set before calling getBeanClass().");
		return getEditorRepository().getFormRepository().getBeanClass(formId);
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
	
	protected String getEditorUrlWithinServlet(String objectId, String parentId,
			String parentEditorId) {
		
		return FormController.getUrl(getId(), objectId, parentId, parentEditorId);
	}

}

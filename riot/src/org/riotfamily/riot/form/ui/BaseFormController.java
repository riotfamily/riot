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
package org.riotfamily.riot.form.ui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.common.web.transaction.TransactionalController;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.FormRepository;
import org.riotfamily.forms.controller.ButtonFactory;
import org.riotfamily.forms.controller.FormSubmissionHandler;
import org.riotfamily.forms.controller.RepositoryFormController;
import org.riotfamily.riot.dao.RiotDao;
import org.riotfamily.riot.editor.EditorConstants;
import org.riotfamily.riot.editor.EditorDefinitionUtils;
import org.riotfamily.riot.editor.EditorRepository;
import org.riotfamily.riot.editor.FormReference;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.editor.ObjectEditorDefinition;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 */
public abstract class BaseFormController extends RepositoryFormController
		implements FormSubmissionHandler, TransactionalController {

	protected static final String EDITOR_DEFINITION_ATTR =
			FormController.class.getName() + ".editorDefinition";

	private EditorRepository editorRepository;

	private String viewName = ResourceUtils.getPath(
			BaseFormController.class, "FormView.ftl");

	public BaseFormController(EditorRepository editorRepository,
			FormRepository formRepository) {

		super(formRepository);
		this.editorRepository = editorRepository;
		ButtonFactory buttonFactory = new ButtonFactory(this);
		buttonFactory.setLabelKey("label.form.button.save");
		buttonFactory.setCssClass("button button-save");
		addButton(buttonFactory);
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	protected String getSessionAttribute(HttpServletRequest request) {
		return BaseFormController.class.getName()
				+ request.getAttribute(EditorConstants.EDITOR_ID);
	}

	protected ObjectEditorDefinition getObjectEditorDefinition(HttpServletRequest request) {
		ObjectEditorDefinition editorDefinition = (ObjectEditorDefinition)
				request.getAttribute(EDITOR_DEFINITION_ATTR);

		if (editorDefinition == null) {
			String editorId = (String) request.getAttribute(EditorConstants.EDITOR_ID);
			Assert.notNull(editorId, "An editorId attribute must be set");
			editorDefinition = (ObjectEditorDefinition) editorRepository.getEditorDefinition(editorId);
			Assert.notNull(editorDefinition, "No such editor: " + editorId);
			request.setAttribute(EDITOR_DEFINITION_ATTR, editorDefinition);
		}
		return editorDefinition;
	}

	protected String getObjectId(HttpServletRequest request) {
		return (String) request.getAttribute(EditorConstants.OBJECT_ID);
	}

	protected String getParentId(HttpServletRequest request) {
		return request.getParameter(EditorConstants.PARENT_ID);
	}

	protected String getFormId(HttpServletRequest request) {
		FormReference ref = (FormReference) getObjectEditorDefinition(request);
		return ref.getFormId();
	}
	
	protected Form createForm(HttpServletRequest request) {
		Form form = super.createForm(request);
		FormUtils.setObjectId(form, getObjectId(request));
		FormUtils.setParentId(form, getParentId(request));
		FormUtils.setEditorDefinition(form, getObjectEditorDefinition(request));
		return form;
	}

	/**
	 * @see org.riotfamily.forms.controller.RepositoryFormController#getFormBackingObject(javax.servlet.http.HttpServletRequest)
	 */
	protected Object getFormBackingObject(HttpServletRequest request) {
		ObjectEditorDefinition editorDefinition = getObjectEditorDefinition(request);
		String objectId = getObjectId(request);
		if (objectId == null) {
			return null;
		}
		return EditorDefinitionUtils.loadBean(editorDefinition, objectId);
	}

	protected Map createModel(Form form, ObjectEditorDefinition formDefinition,
			HttpServletRequest request, HttpServletResponse response) {

		HashMap model = new HashMap();
		model.put(EditorConstants.EDITOR_ID, formDefinition.getId());
		model.put(EditorConstants.PARENT_ID, FormUtils.getParentId(form));
		model.put(EditorConstants.OBJECT_ID, FormUtils.getObjectId(form));
		model.put("formId", form.getId());
		return model;
	}

	/**
	 * @see org.riotfamily.forms.controller.AbstractFormController#showForm(org.riotfamily.forms.Form, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected ModelAndView showForm(Form form, HttpServletRequest request,
			HttpServletResponse response) {

		StringWriter sw = new StringWriter();
		renderForm(form, new PrintWriter(sw));
		Map model = createModel(form, getObjectEditorDefinition(request),
				request, response);

		model.put("form", sw.toString());
		return new ModelAndView(viewName, model);
	}


	public final ModelAndView handleFormSubmission(Form form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Object bean = form.populateBackingObject();
		ObjectEditorDefinition editorDef = getObjectEditorDefinition(request);

		ListDefinition listDef = EditorDefinitionUtils.getParentListDefinition(editorDef);
		RiotDao dao = listDef.getListConfig().getDao();

		if (form.isNew()) {
			log.debug("Saving entity ...");
			String parentId = FormUtils.getParentId(form);
			Object parent = EditorDefinitionUtils.loadParent(listDef, parentId);
			dao.save(bean, parent);
			FormUtils.setObjectId(form, dao.getObjectId(bean));
			form.setValue(bean);
			return afterSave(form, editorDef, request, response);
		}
		else {
			log.debug("Updating entity ...");
			dao.update(bean);
			return afterUpdate(form, editorDef, request, response);
		}
	}

	protected abstract ModelAndView afterSave(Form form, ObjectEditorDefinition editorDefinition,
			HttpServletRequest request, HttpServletResponse response);

	protected abstract ModelAndView afterUpdate(Form form, ObjectEditorDefinition editorDefinition,
			HttpServletRequest request, HttpServletResponse response);

}

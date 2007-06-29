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

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.FormRepository;
import org.riotfamily.riot.editor.EditorDefinitionUtils;
import org.riotfamily.riot.editor.EditorRepository;
import org.riotfamily.riot.editor.FormDefinition;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.list.ui.ListService;
import org.riotfamily.riot.list.ui.ListSession;
import org.riotfamily.riot.security.AccessController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

/**
 *
 */
public class FormController extends BaseFormController {

	private static final String PARAM_SAVED = "saved";

	private ListService listService;

	public FormController(EditorRepository editorRepository,
			FormRepository formRepository, ListService listService) {

		super(editorRepository, formRepository);
		this.listService = listService;
	}

	protected Map createModel(Form form, FormDefinition formDefinition,
			HttpServletRequest request, HttpServletResponse response) {

		Map model = super.createModel(form, formDefinition, request, response);
		model.put(PARAM_SAVED, Boolean.valueOf(request.getParameter(PARAM_SAVED) != null));

		Object object = null;
		if (!form.isNew()) {
			object = form.getBackingObject();
		}
		try {
			if (!AccessController.isGranted("view", object, formDefinition)) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
			}
		}
		catch (IOException e) {
			log.error("Error sending forbidden error for formDefinition[ "
					+ formDefinition.getName() + " ]");
		}

		model.put("childLists", formDefinition.getChildEditorReferences(object,
				form.getFormContext().getMessageResolver()));

		ListDefinition parentListDef = EditorDefinitionUtils
				.getParentListDefinition(formDefinition);

		if (parentListDef != null) {
			ListSession session = listService.getOrCreateListSession(
				parentListDef.getId(), FormUtils.getParentId(form),
				null, request);

			model.put("listKey", session.getKey());
		}

		return model;
	}

	protected ModelAndView afterSave(Form form, FormDefinition formDefinition,
			HttpServletRequest request, HttpServletResponse response) {

		if (!formDefinition.getChildEditorDefinitions().isEmpty()) {
			return reloadForm(form, formDefinition);
		}
		else {
			return showParentList(form, formDefinition);
		}
	}

	protected ModelAndView afterUpdate(Form form, FormDefinition formDefinition,
			HttpServletRequest request, HttpServletResponse response) {

		return showParentList(form, formDefinition);
	}

	protected ModelAndView showParentList(Form form,
			FormDefinition formDefinition) {

		String listUrl = formDefinition.createEditorPath(
				form.getBackingObject(),
				form.getFormContext().getMessageResolver())
				.getParent().getEditorUrl();

		return new ModelAndView(new RedirectView(listUrl, true));
	}

	protected ModelAndView reloadForm(Form form,
			FormDefinition formDefinition) {

		String formUrl = formDefinition.createEditorPath(
				form.getBackingObject(),
				form.getFormContext().getMessageResolver())
				.getEditorUrl();

		formUrl = ServletUtils.addParameter(formUrl, "saved", "true");
		return new ModelAndView(new RedirectView(formUrl, true));
	}
}

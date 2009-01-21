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
import org.riotfamily.forms.factory.FormRepository;
import org.riotfamily.riot.editor.ObjectEditorDefinition;
import org.riotfamily.riot.editor.EditorDefinitionUtils;
import org.riotfamily.riot.editor.EditorRepository;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.list.ui.ListService;
import org.riotfamily.riot.list.ui.ListSession;
import org.riotfamily.riot.security.AccessController;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

/**
 *
 */
public class FormController extends BaseFormController {

	private static final String PARAM_SAVED = "saved";

	private ListService listService;

	public FormController(EditorRepository editorRepository,
			FormRepository formRepository,
			PlatformTransactionManager transactionManager,
			ListService listService) {

		super(editorRepository, formRepository, transactionManager);
		this.listService = listService;
	}

	protected Map<String, Object> createModel(Form form, ObjectEditorDefinition editorDefinition,
			HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> model = super.createModel(form, editorDefinition, request, response);
		model.put(PARAM_SAVED, Boolean.valueOf(request.getParameter(PARAM_SAVED) != null));

		Object object = null;
		if (!form.isNew()) {
			object = form.getBackingObject();
		}
		
		if (!AccessController.isGranted("view", object)
				|| !AccessController.isGranted("use-editor", editorDefinition)) {
			
			try {
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
			}
			catch (IOException e) {
			}
			return null;
		}
		

		model.put("childLists", editorDefinition.getChildEditorReferences(object,
				form.getFormContext().getMessageResolver()));

		ListDefinition listDef = EditorDefinitionUtils
				.getListDefinition(editorDefinition);

		if (listDef != null) {
			ListSession session = listService.getOrCreateListSession(
				listDef.getId(), FormUtils.getParentId(form),
				FormUtils.getParentEditorId(form), null, request);

			model.put("listKey", session.getKey());
		}

		return model;
	}

	protected ModelAndView afterSave(Form form, ObjectEditorDefinition editorDefinition,
			HttpServletRequest request, HttpServletResponse response) {
		
		if (request.getParameter("stayInForm") != null 
				|| !editorDefinition.getChildEditorDefinitions().isEmpty()) {
			
			return reloadForm(form, editorDefinition);
		}
		else {
			return showParentList(form, editorDefinition);
		}
	}

	protected ModelAndView afterUpdate(Form form, ObjectEditorDefinition editorDefinition,
			HttpServletRequest request, HttpServletResponse response) {

		if (request.getParameter("stayInForm") != null) {
			return reloadForm(form, editorDefinition);
		}
		else {
			return showParentList(form, editorDefinition);
		}
	}

	protected ModelAndView showParentList(Form form,
			ObjectEditorDefinition editorDefinition) {

		String listUrl = EditorDefinitionUtils.getListUrl(
				editorDefinition,
				FormUtils.getObjectId(form),
				FormUtils.getParentId(form),
				FormUtils.getParentEditorId(form),
				form.getFormContext().getMessageResolver());

		return new ModelAndView(new RedirectView(listUrl, true));
	}

	protected ModelAndView reloadForm(Form form,
			ObjectEditorDefinition editorDefinition) {

		String formUrl = editorDefinition.createEditorPath(
				form.getBackingObject(),
				form.getFormContext().getMessageResolver())
				.getEditorUrl();

		formUrl = ServletUtils.addParameter(formUrl, "saved", "true");
		return new ModelAndView(new RedirectView(formUrl, true));
	}
	
	public static String getUrl(String editorId, String objectId, 
			String parentId, String parentEditorId) {
		
		StringBuffer sb = new StringBuffer();
		sb.append("/form/").append(editorId);
		if (objectId != null) {
			sb.append('/').append(objectId);
		}
		if (parentId != null) {
			sb.append("?parentId=").append(parentId);
			if (parentEditorId != null) {
				sb.append("&parentEditorId=").append(parentEditorId);
			}
		}
		return sb.toString();
	}
}

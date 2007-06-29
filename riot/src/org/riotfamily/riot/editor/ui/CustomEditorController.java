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
package org.riotfamily.riot.editor.ui;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.common.web.transaction.TransactionalController;
import org.riotfamily.riot.editor.CustomEditorDefinition;
import org.riotfamily.riot.editor.EditorConstants;
import org.riotfamily.riot.editor.EditorRepository;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;

public class CustomEditorController implements TransactionalController {

	private EditorRepository editorRepository;

	private String viewName = ResourceUtils.getPath(
			CustomEditorController.class, "CustomEditorView.ftl");


	public CustomEditorController(EditorRepository repository) {
		this.editorRepository = repository;
	}

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String editorId = (String) request.getAttribute(EditorConstants.EDITOR_ID);
		Assert.notNull(editorId, "No editorId in request scope");

		CustomEditorDefinition editorDef = (CustomEditorDefinition)
				editorRepository.getEditorDefinition(editorId);

		Assert.notNull(editorDef, "No such editor: " + editorId);

		String objectId = request.getParameter(EditorConstants.OBJECT_ID);
		String parentId = request.getParameter(EditorConstants.PARENT_ID);

		HashMap model = new HashMap();
		model.put(EditorConstants.EDITOR_ID, editorId);
		model.put(EditorConstants.OBJECT_ID, objectId);
		model.put(EditorConstants.PARENT_ID, parentId);
		model.put("editorUrl", editorDef.getTargetUrl(objectId, parentId));

		return new ModelAndView(viewName, model);
	}

}

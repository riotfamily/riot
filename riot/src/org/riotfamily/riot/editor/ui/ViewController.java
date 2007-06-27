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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.transaction.TransactionalController;
import org.riotfamily.riot.editor.EditorDefinitionUtils;
import org.riotfamily.riot.editor.EditorRepository;
import org.riotfamily.riot.editor.ViewDefinition;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;

public class ViewController implements TransactionalController {

	private EditorRepository editorRepository;

	public ViewController(EditorRepository repository) {
		this.editorRepository = repository;
	}

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String editorId = (String) request.getAttribute("editorId");
		Assert.notNull(editorId, "No editorId in request scope");

		String objectId = (String) request.getAttribute("objectId");
		Assert.notNull(objectId, "No objectId in request scope");

		ViewDefinition viewDef = (ViewDefinition)
				editorRepository.getEditorDefinition(editorId);

		Assert.notNull(viewDef, "No such ViewDefinition: " + editorId);

		Object object = EditorDefinitionUtils.loadBean(viewDef, objectId);
		return new ModelAndView(viewDef.getViewName(), "object", object);
	}

}

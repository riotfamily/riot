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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.form.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.factory.FormRepository;
import org.riotfamily.riot.editor.EditorRepository;
import org.riotfamily.riot.editor.ObjectEditorDefinition;
import org.riotfamily.riot.list.ui.ListService;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.servlet.ModelAndView;

public class PopupFormController extends FormController {

	private String closeWindowViewName = ResourceUtils.getPath(
			PopupFormController.class, "PopupFormViewSuccess.ftl");
			
	public PopupFormController(EditorRepository editorRepository,
			FormRepository formRepository,
			PlatformTransactionManager transactionManager,
			ListService listService) {

		super(editorRepository, formRepository, transactionManager, listService);
		setViewName(ResourceUtils.getPath(PopupFormController.class, "PopupFormView.ftl"));
	}
	
	@Override
	protected ModelAndView afterSave(Form form, Object bean,
			ObjectEditorDefinition editorDefinition,
			HttpServletRequest request, HttpServletResponse response) {
		
		return closeWindow();
	}
	
	@Override
	protected ModelAndView afterUpdate(Form form, Object bean,
			ObjectEditorDefinition editorDefinition,
			HttpServletRequest request, HttpServletResponse response) {
		
		return closeWindow();
	}

	private ModelAndView closeWindow() {
		return new ModelAndView(closeWindowViewName);
	}
	
}

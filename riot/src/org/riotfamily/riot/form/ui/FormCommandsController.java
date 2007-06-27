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

import java.util.HashMap;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.i18n.AdvancedMessageCodesResolver;
import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.common.web.transaction.TransactionalController;
import org.riotfamily.riot.editor.EditorDefinitionUtils;
import org.riotfamily.riot.editor.EditorRepository;
import org.riotfamily.riot.editor.FormDefinition;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.list.ui.ListService;
import org.riotfamily.riot.list.ui.ListSession;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class FormCommandsController implements TransactionalController,
		MessageSourceAware {

	private EditorRepository editorRepository;

	private ListService listService;

	private AdvancedMessageCodesResolver messageCodesResolver;

	private MessageSource messageSource;

	private String viewName = ResourceUtils.getPath(
			FormCommandsController.class, "FormCommandsView.ftl");

	public FormCommandsController(EditorRepository editorRepository,
			ListService listService,
			AdvancedMessageCodesResolver messageCodesResolver) {

		this.editorRepository = editorRepository;
		this.listService = listService;
		this.messageCodesResolver = messageCodesResolver;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		HashMap model = new HashMap();

		String editorId = (String) request.getAttribute("editorId");
		Assert.notNull(editorId, "An editorId attribute must be set");

		String objectId = (String) request.getAttribute("objectId");
		String parentId = request.getParameter("parentId");

		model.put("editorId", editorId);
		model.put("parentId", parentId);
		model.put("objectId", objectId);

		FormDefinition formDefinition = editorRepository.getFormDefinition(editorId);
		Assert.notNull(formDefinition, "No such editor: " + editorId);

		Object bean = null;
		if (objectId != null) {
			bean = EditorDefinitionUtils.loadBean(formDefinition, objectId);
		}

		Locale locale = RequestContextUtils.getLocale(request);
		MessageResolver messageResolver = new MessageResolver(messageSource,
				messageCodesResolver, locale);

		model.put("childLists", formDefinition.getChildEditorReferences(
				bean, messageResolver));

		ListDefinition parentListDef = EditorDefinitionUtils
				.getParentListDefinition(formDefinition);

		if (parentListDef != null) {
			ListSession session = listService.getOrCreateListSession(
				parentListDef.getId(), parentId,
				null, request);

			model.put("listKey", session.getKey());
		}

		return new ModelAndView(viewName, model);
	}

}

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

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.i18n.AdvancedMessageCodesResolver;
import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.riot.editor.EditorConstants;
import org.riotfamily.riot.editor.EditorDefinitionUtils;
import org.riotfamily.riot.editor.EditorRepository;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.editor.ViewDefinition;
import org.riotfamily.riot.editor.ViewReference;
import org.riotfamily.riot.list.ui.ListService;
import org.riotfamily.riot.list.ui.ListSession;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.support.RequestContextUtils;

public class ViewController implements Controller, MessageSourceAware {

	private MessageSource messageSource;
	
	private AdvancedMessageCodesResolver messageCodesResolver;
	
	private EditorRepository editorRepository;

	private ListService listService;

	private String viewName = ResourceUtils.getPath(
			ViewController.class, "ReadOnlyView.ftl");

	public ViewController(EditorRepository repository,
			ListService listService) {

		this.editorRepository = repository;
		this.listService = listService;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	public void setMessageCodesResolver(AdvancedMessageCodesResolver codesResolver) {
		this.messageCodesResolver = codesResolver;
	}
	
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public final ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String editorId = (String) request.getAttribute(EditorConstants.EDITOR_ID);
		Assert.notNull(editorId, "No editorId in request scope");

		String objectId = (String) request.getAttribute(EditorConstants.OBJECT_ID);
		Assert.notNull(objectId, "No objectId in request scope");

		ViewDefinition editorDef = (ViewDefinition) editorRepository.getEditorDefinition(editorId);
		Assert.notNull(editorDef, "No such EditorDefinition: " + editorId);

		Object object = EditorDefinitionUtils.loadBean(editorDef, objectId);
		String parentId = EditorDefinitionUtils.getParentId(editorDef, object);
		
		ModelAndView mv = new ModelAndView(viewName);
		mv.addObject(EditorConstants.EDITOR_ID, editorId);
		mv.addObject(EditorConstants.OBJECT_ID, objectId);
		mv.addObject(EditorConstants.PARENT_ID, parentId);
		mv.addObject("object", object);
		mv.addObject("template", ((ViewReference) editorDef).getTemplate());

		Locale locale = RequestContextUtils.getLocale(request);
		MessageResolver messageResolver = new MessageResolver(messageSource,
				messageCodesResolver, locale);
		
		mv.addObject("childLists", editorDef.getChildEditorReferences(object,
				messageResolver));

		ListDefinition listDef = EditorDefinitionUtils.getListDefinition(editorDef);

		if (listDef != null) {
			ListSession session = listService.getOrCreateListSession(
				listDef.getId(), parentId, null, null, request);

			mv.addObject("listKey", session.getKey());
			mv.addObject(EditorConstants.PARENT_ID, session.getParentId());
		}

		return mv;
	}
	
	public static String getUrl(String editorId, String objectId) {
		StringBuffer sb = new StringBuffer();
		sb.append("/view/").append(editorId);
		if (objectId != null) {
			sb.append('/').append(objectId);
		}
		return sb.toString();
	}

}

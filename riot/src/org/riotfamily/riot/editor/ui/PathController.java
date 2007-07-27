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

import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.riot.editor.EditorConstants;
import org.riotfamily.riot.editor.EditorDefinition;
import org.riotfamily.riot.editor.EditorRepository;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Controller that displays a breadcrumb navigation for an editor.
 */
public class PathController implements Controller, MessageSourceAware {

	private EditorRepository repository;

	private MessageSource messageSource;

	private String viewName = ResourceUtils.getPath(
			PathController.class, "PathView.ftl");

	private String modelKey = "path";

	public PathController(EditorRepository repository) {
		this.repository = repository;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public String getModelKey() {
		return modelKey;
	}

	public void setModelKey(String modelKey) {
		this.modelKey = modelKey;
	}

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public EditorRepository getRepository() {
		return repository;
	}

	/**
	 */
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		EditorPath path;
		String editorId = request.getParameter(EditorConstants.EDITOR_ID);

		EditorDefinition editor = repository.getEditorDefinition(editorId);

		String objectId = request.getParameter(EditorConstants.OBJECT_ID);
		String parentId = request.getParameter(EditorConstants.PARENT_ID);

		EditorReference lastComponent = createLastPathComponent(
				editor, objectId, parentId,
				new MessageResolver(messageSource,
				repository.getMessageCodesResolver(),
				RequestContextUtils.getLocale(request)));

		path = new EditorPath(editorId, objectId, parentId,
				lastComponent);

		path.setSubPage(request.getParameter("subPage"));
		path.encodeUrls(response);

		return new ModelAndView(viewName, modelKey, path);
	}

	protected EditorReference createLastPathComponent(
			final EditorDefinition editor, final String objectId,
			final String parentId, final MessageResolver messageResolver) {

		return editor.createEditorPath(objectId, parentId, messageResolver);
	}
}

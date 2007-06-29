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
package org.riotfamily.riot.list.ui;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.riot.editor.EditorConstants;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * Controller that displays lists defined in the ListRepository.
 */
public class ListController implements Controller {

	protected Log log = LogFactory.getLog(ListController.class);

	private String viewName = ResourceUtils.getPath(
			ListController.class, "ListView.ftl");

	private ListService listService;

	public void setListService(ListService listService) {
		this.listService = listService;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	protected String getViewName() {
		return viewName;
	}

	public final ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String editorId = (String) request.getAttribute(EditorConstants.EDITOR_ID);
		String parentId = (String) request.getAttribute(EditorConstants.PARENT_ID);
		String choose = request.getParameter("choose");

		ListSession session = listService.getOrCreateListSession(
				editorId, parentId, choose, request);

		HashMap model = new HashMap();
		model.put(EditorConstants.EDITOR_ID, editorId);
		model.put(EditorConstants.PARENT_ID, parentId);
		model.put("filterForm", session.getFilterFormHtml());
		model.put("search", session.getSearchProperties());
		model.put("searchQuery", session.getSearchQuery());
		model.put("hasCommands", Boolean.valueOf(session.hasListCommands()));
		model.put("listKey", session.getKey());
		model.put("title", session.getTitle());

		return new ModelAndView(viewName, model);
	}

}

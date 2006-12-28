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
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.list.ui;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.common.web.mapping.UrlMapping;
import org.riotfamily.common.web.mapping.UrlMappingAware;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.editor.ui.EditorController;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * Controller that displays lists defined in the ListRepository.
 */
public class ListController implements Controller, 
		UrlMappingAware, BeanNameAware, EditorController {
	
	protected Log log = LogFactory.getLog(ListController.class);
	
	private String editorIdAttribute = "editorId";

	private String parentIdAttribute = "parentId";

	private String viewName = ResourceUtils.getPath(
			ListController.class, "ListView.ftl");

	private UrlMapping urlMapping;
	
	private String beanName;
	
	private ListService listService;

	public void setListService(ListService listService) {
		this.listService = listService;
	}

	public void setEditorIdAttribute(String editorIdAttribute) {
		this.editorIdAttribute = editorIdAttribute;
	}

	protected String getEditorIdAttribute() {
		return editorIdAttribute;
	}

	public void setParentIdAttribute(String parentIdAttribute) {
		this.parentIdAttribute = parentIdAttribute;
	}

	protected String getParentIdAttribute() {
		return parentIdAttribute;
	}
	
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	protected String getViewName() {
		return viewName;
	}
	
	public void setUrlMapping(UrlMapping urlMapping) {
		this.urlMapping = urlMapping;
	}
	
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
		
	public final ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String editorId = (String) request.getAttribute(editorIdAttribute);
		String parentId = (String) request.getAttribute(parentIdAttribute);
		
		HashMap model = new HashMap();
		model.put("filterForm", listService.getFilterForm(editorId, parentId, request));
		model.put("commands", listService.getListCommands(editorId, parentId, request));
		model.put(editorIdAttribute, editorId);
		model.put(parentIdAttribute, parentId);
		return new ModelAndView(viewName, model);
	}
	
	public Class getDefinitionClass() {
		return ListDefinition.class;
	}
		
	public String getUrl(String editorId, String objectId, String parentId) {
		HashMap attrs = new HashMap();
		attrs.put(getEditorIdAttribute(), editorId);
		if (parentId != null) {
			attrs.put(getParentIdAttribute(), parentId);
		}
		return urlMapping.getUrl(beanName, attrs);
	}

}

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

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.collection.FlatMap;
import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.common.web.view.freemarker.ResourceTemplateLoader;
import org.riotfamily.riot.editor.EditorConstants;
import org.riotfamily.riot.editor.EditorDefinition;
import org.riotfamily.riot.editor.EditorDefinitionUtils;
import org.riotfamily.riot.editor.EditorRepository;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.editor.ViewReference;
import org.riotfamily.riot.list.ui.ListService;
import org.riotfamily.riot.list.ui.ListSession;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import freemarker.template.Configuration;

public class ViewController implements Controller,
		ResourceLoaderAware, InitializingBean {

	private EditorRepository editorRepository;

	private ResourceLoader resourceLoader;

	private ListService listService;

	private String viewName = ResourceUtils.getPath(
			ViewController.class, "ReadOnlyView.ftl");

	private Configuration configuration;

	public ViewController(EditorRepository repository,
			ListService listService) {

		this.editorRepository = repository;
		this.listService = listService;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public void afterPropertiesSet() throws Exception {
		configuration = new Configuration();
		ResourceTemplateLoader loader = new ResourceTemplateLoader();
		loader.setResourceLoader(resourceLoader);
		configuration.setTemplateLoader(loader);
	}

	public final ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String editorId = (String) request.getAttribute(EditorConstants.EDITOR_ID);
		Assert.notNull(editorId, "No editorId in request scope");

		String objectId = (String) request.getAttribute(EditorConstants.OBJECT_ID);
		Assert.notNull(objectId, "No objectId in request scope");

		EditorDefinition editorDef = editorRepository.getEditorDefinition(editorId);
		Assert.notNull(editorDef, "No such EditorDefinition: " + editorId);

		StringWriter sw = new StringWriter();

		Object object = EditorDefinitionUtils.loadBean(editorDef, objectId);
		Map model = new FlatMap();
		model.put("object", object);
		model.put("request", request);
		try {
			String template = ((ViewReference) editorDef).getTemplate();
			configuration.getTemplate(template).process(model, sw);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}

		model = new HashMap();
		model.put(EditorConstants.EDITOR_ID, editorId);
		model.put(EditorConstants.OBJECT_ID, objectId);
		model.put("form", sw.toString());

		ListDefinition parentListDef = EditorDefinitionUtils
				.getParentListDefinition(editorDef);

		if (parentListDef != null) {
			ListSession session = listService.getOrCreateListSession(
				parentListDef.getId(),
				EditorDefinitionUtils.getParentId(editorDef, object),
				null, null, request);

			model.put("listKey", session.getKey());
		}

		return new ModelAndView(viewName, model);
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

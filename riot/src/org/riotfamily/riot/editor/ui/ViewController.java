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
package org.riotfamily.riot.editor.ui;

import java.io.StringWriter;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.collection.FlatMap;
import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.common.web.mapping.UrlMapping;
import org.riotfamily.common.web.mapping.UrlMappingAware;
import org.riotfamily.common.web.view.freemarker.ResourceTemplateLoader;
import org.riotfamily.riot.editor.EditorDefinitionUtils;
import org.riotfamily.riot.editor.EditorRepository;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.editor.ViewDefinition;
import org.riotfamily.riot.list.ui.ListService;
import org.riotfamily.riot.list.ui.ListSession;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import freemarker.template.Configuration;

public class ViewController implements Controller, ResourceLoaderAware, 
		InitializingBean, EditorController, UrlMappingAware, BeanNameAware {

	private EditorRepository editorRepository;
	
	private ResourceLoader resourceLoader;
	
	private PlatformTransactionManager transactionManager;
	
	private ListService listService;
	
	private String editorIdAttribute = "editorId";
	
	private String objectIdAttribute = "objectId";
	
	private String viewName = ResourceUtils.getPath(
			ViewController.class, "ReadOnlyView.ftl");
	
	private UrlMapping urlMapping;
	
	private String beanName;
	
	private Configuration configuration;
	
	public ViewController(EditorRepository repository, 
			PlatformTransactionManager transactionManager,
			ListService listService) {
		
		this.editorRepository = repository;
		this.transactionManager = transactionManager;
		this.listService = listService;
	}	

	public void setEditorIdAttribute(String editorIdAttribute) {
		this.editorIdAttribute = editorIdAttribute;
	}
	
	public void setObjectIdAttribute(String objectIdAttribute) {
		this.objectIdAttribute = objectIdAttribute;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}	
	
	public void setUrlMapping(UrlMapping urlMapping) {
		this.urlMapping = urlMapping;
	}
	
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public void afterPropertiesSet() throws Exception {
		configuration = new Configuration();
		ResourceTemplateLoader loader = new ResourceTemplateLoader();
		loader.setResourceLoader(resourceLoader);
		configuration.setTemplateLoader(loader);
	}
	
	public final ModelAndView handleRequest(final HttpServletRequest request,
			final HttpServletResponse response) throws Exception {

		String editorId = (String) request.getAttribute(editorIdAttribute);
		Assert.notNull(editorId, "No editorId in request scope");
		
		final String objectId = (String) request.getAttribute(objectIdAttribute);
		Assert.notNull(objectId, "No objectId in request scope");
		
		final ViewDefinition viewDef = (ViewDefinition)
				editorRepository.getEditorDefinition(editorId);
		
		Assert.notNull(viewDef, "No such ViewDefinition: " + editorId);
		
		final StringWriter sw = new StringWriter();
		
		Object bean = new TransactionTemplate(transactionManager).execute(new TransactionCallback() {
			public Object doInTransaction(TransactionStatus ts) {
				Object object = EditorDefinitionUtils.loadBean(viewDef, objectId);
				FlatMap model = new FlatMap();
				model.put("object", object);
				model.put("request", request);
				try {
					configuration.getTemplate(viewDef.getTemplate()).process(model, sw);
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
				return object;
			}
		});
		
		HashMap model = new HashMap();
		model.put("editorId", editorId);
		model.put("objectId", objectId);
		model.put("form", sw.toString());
		
		ListDefinition parentListDef = EditorDefinitionUtils
				.getParentListDefinition(viewDef);
		
		if (parentListDef != null) {
			ListSession session = listService.getOrCreateListSession(
				parentListDef.getId(), 
				EditorDefinitionUtils.getParentId(viewDef, bean), 
				null, request);
			
			model.put("listKey", session.getKey());
		}

		return new ModelAndView(viewName, model);
	}
	
	public Class getDefinitionClass() {
		return ViewDefinition.class;
	}
	
	public String getUrl(String editorId, String objectId, String parentId) {
		FlatMap attrs = new FlatMap();
		attrs.put(editorIdAttribute, editorId);
		attrs.put(objectIdAttribute, objectId);
		return urlMapping.getUrl(beanName, attrs);
	}	
		
}

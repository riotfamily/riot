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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.collection.FlatMap;
import org.riotfamily.common.i18n.AdvancedMessageCodesResolver;
import org.riotfamily.common.i18n.MessageResolver;
import org.riotfamily.common.util.ResourceUtils;
import org.riotfamily.common.web.mapping.UrlMapping;
import org.riotfamily.common.web.mapping.UrlMappingAware;
import org.riotfamily.common.web.view.freemarker.ResourceTemplateLoader;
import org.riotfamily.riot.editor.AbstractDisplayDefinition;
import org.riotfamily.riot.editor.EditorDefinitionUtils;
import org.riotfamily.riot.editor.EditorRepository;
import org.riotfamily.riot.editor.ListDefinition;
import org.riotfamily.riot.editor.ViewDefinition;
import org.riotfamily.riot.form.command.FormCommand;
import org.riotfamily.riot.form.command.FormCommandContext;
import org.riotfamily.riot.list.ListConfig;
import org.riotfamily.riot.list.ListRepository;
import org.riotfamily.riot.list.command.Command;
import org.riotfamily.riot.list.command.support.CommandExecutor;
import org.riotfamily.riot.list.ui.render.CommandRenderer;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.support.RequestContextUtils;

import freemarker.template.Configuration;

public class ViewController implements Controller, ResourceLoaderAware, 
		InitializingBean, EditorController, MessageSourceAware, 
		UrlMappingAware, BeanNameAware {

	private EditorRepository editorRepository;
	
	private ListRepository listRepository;
	
	private ResourceLoader resourceLoader;
	
	private AdvancedMessageCodesResolver messageCodesResolver;
	
	private MessageSource messageSource;
	
	private PlatformTransactionManager transactionManager;
	
	private CommandExecutor commandExecutor;
	
	private String editorIdAttribute = "editorId";
	
	private String objectIdAttribute = "objectId";
	
	private String viewName = ResourceUtils.getPath(
			ViewController.class, "ReadOnlyView.ftl");
	
	private UrlMapping urlMapping;
	
	private String beanName;
	
	private Configuration configuration;
	
	public ViewController(EditorRepository repository, 
			ListRepository listRepository, 
			PlatformTransactionManager transactionManager,
			CommandExecutor commandExecutor) {
		
		this.editorRepository = repository;
		this.listRepository = listRepository;
		this.transactionManager = transactionManager;
		this.commandExecutor = commandExecutor;
	}	

	public void setMessageCodesResolver(
			AdvancedMessageCodesResolver messageCodesResolver) {
		this.messageCodesResolver = messageCodesResolver;
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
	
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
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
		
		Object object = new TransactionTemplate(transactionManager).execute(new TransactionCallback() {
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
		
		final FlatMap viewModel = new FlatMap();
		viewModel.put("editorId", editorId);
		viewModel.put("objectId", objectId);
		viewModel.put("form", sw.toString());
		
		ListDefinition parentList = EditorDefinitionUtils.getParentListDefinition(viewDef);
		
		if (parentList != null && listRepository != null) {
			ListConfig listConfig = listRepository.getListConfig(
					parentList.getListId());
					
			ArrayList commands = new ArrayList();
			CommandRenderer renderer = new CommandRenderer();
			renderer.setRenderText(true);
			
			Locale locale = RequestContextUtils.getLocale(request);
			
			final FormCommandContext context = getFormCommandContext(viewDef, 
					locale, listConfig, object, request, response);
			
			Iterator it = listConfig.getColumnCommands().iterator();
			while (it.hasNext()) {
				Command command = (Command) it.next();
				if (command instanceof FormCommand) {
					context.setCommand(command);
					StringWriter commandSw = new StringWriter();
					PrintWriter pw = new PrintWriter(commandSw);
					renderer.render(context, pw);
					commands.add(commandSw.toString());					
				}
			}
			viewModel.put("commands", commands);
			if (isCommandRequest(request)) {
				new TransactionTemplate(transactionManager).execute(new TransactionCallbackWithoutResult() {
					protected void doInTransactionWithoutResult(TransactionStatus ts) {
						commandExecutor.executeCommand(listRepository, context, 
								request, response, viewModel);
					}
				});
				return null;
			}
		}		
		
		return new ModelAndView(viewName, viewModel);
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
	
	protected boolean isCommandRequest(HttpServletRequest request) {
		return request.getParameter("command") != null;
	}
	
	protected FormCommandContext getFormCommandContext(AbstractDisplayDefinition add, Locale locale,
			ListConfig listConfig, Object bean, HttpServletRequest request, HttpServletResponse response) {	
		return new FormCommandContext(
				add, new MessageResolver(messageSource, messageCodesResolver, locale), locale, listConfig, bean, 
				request, response);
	}
}

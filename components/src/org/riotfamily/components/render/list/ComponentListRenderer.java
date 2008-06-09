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
package org.riotfamily.components.render.list;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.components.EditModeUtils;
import org.riotfamily.components.config.ComponentListConfig;
import org.riotfamily.components.config.ComponentRepository;
import org.riotfamily.components.context.ComponentListRequestContext;
import org.riotfamily.components.context.ComponentRequestUtils;
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.ComponentList;
import org.riotfamily.components.model.Content;
import org.riotfamily.components.model.ContentContainer;
import org.riotfamily.riot.security.AccessController;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

public class ComponentListRenderer {

	public static final String PARENT_ATTRIBUTE = 
			ComponentListRenderer.class.getName() + ".parent";
	
	private ComponentDao componentDao;

	private ComponentRepository componentRepository;
	
	private PlatformTransactionManager transactionManager;
	
	private RenderStrategy liveModeRenderStrategy;
	
	private RenderStrategy editModeRenderStrategy;

	
	public ComponentListRenderer(ComponentDao componentDao, 
			ComponentRepository componentRepository,
			PlatformTransactionManager transactionManager) {
		
		this.componentDao = componentDao;
		this.componentRepository = componentRepository;
		this.transactionManager = transactionManager;
	}

	public void setLiveModeRenderStrategy(RenderStrategy liveModeRenderStrategy) {
		this.liveModeRenderStrategy = liveModeRenderStrategy;
	}

	public void setEditModeRenderStrategy(RenderStrategy editModeRenderStrategy) {
		this.editModeRenderStrategy = editModeRenderStrategy;
	}
	

	private ComponentList createList(ContentContainer container, 
			Content content, String key, ComponentListConfig config) {
		
		final ComponentList list = new ComponentList();
		list.setContainer(container);
		content.setValue(key, list);
		for (String type : config.getInitialComponentTypes()) {
			Component component = componentRepository.createComponent(type, null);
			list.appendComponent(component);
		}
		new TransactionTemplate(transactionManager).execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				componentDao.saveComponentList(list);
			}
		});
		return list;
	}
	
	public void renderComponentList(ContentContainer container, 
			String key, ComponentListConfig config,
			HttpServletRequest request,
			HttpServletResponse response) 
			throws Exception {

		ComponentList list;
		RenderStrategy strategy = liveModeRenderStrategy;
		if (EditModeUtils.isEditMode(request)) {
			list = (ComponentList) container.getPreviewVersion().getValue(key);
			if (list == null) {
				list = createList(container, container.getPreviewVersion(), key, config);
			}
			
			if (AccessController.isGranted("edit", list.getContainer())) {
				strategy = editModeRenderStrategy;
			}
			
			ComponentListRequestContext context = ComponentRequestUtils.createContext(request, list.getId());
			if (context != null) {
				context.setComponentListConfig(config);
				ComponentRequestUtils.storeContext(context, request, 120000);
			}
		}
		else {
			list = (ComponentList) container.getLiveVersion().getValue(key);
		}
		if (list != null) {
			strategy.render(list, config, request, response);
		}
	}
	
	public void renderNestedComponentList(Component component, 
			String key, ComponentListConfig config,
			HttpServletRequest request,
			HttpServletResponse response) 
			throws Exception {

		ComponentList list;
		RenderStrategy strategy = liveModeRenderStrategy;
		request.setAttribute(PARENT_ATTRIBUTE, component);
		if (EditModeUtils.isEditMode(request)) {
			list = (ComponentList) component.getValue(key);
			if (list == null) {
				list = createList(component.getList().getContainer(), component, key, config);
			}
			if (AccessController.isGranted("edit", list.getContainer())) {
				strategy = editModeRenderStrategy;
			}
			ComponentListRequestContext context = ComponentRequestUtils.createContext(request, list.getId());
			if (context != null) {
				context.setComponentListConfig(config);
				ComponentRequestUtils.storeContext(context, request, 120000);
			}
		}
		else {
			list = (ComponentList) component.getValue(key);
		}
		if (list != null) {
			strategy.render(list, config, request, response);
		}
		request.removeAttribute(PARENT_ATTRIBUTE);
	}

}

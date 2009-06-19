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

import java.io.StringWriter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.SpringUtils;
import org.riotfamily.common.web.util.CapturingResponseWrapper;
import org.riotfamily.components.config.ComponentListConfig;
import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.ComponentList;
import org.riotfamily.components.model.Content;
import org.riotfamily.components.model.ContentContainer;
import org.riotfamily.components.support.EditModeUtils;
import org.riotfamily.core.security.AccessController;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.WebApplicationContext;

public class ComponentListRenderer implements ServletContextAware {

	public static final String PARENT_ATTRIBUTE = 
			ComponentListRenderer.class.getName() + ".parent";
	
	private PlatformTransactionManager transactionManager;
	
	private RenderStrategy liveModeRenderStrategy;
	
	private RenderStrategy editModeRenderStrategy;

	private String riotServletName = "riot";

	
	public ComponentListRenderer(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setLiveModeRenderStrategy(RenderStrategy liveModeRenderStrategy) {
		this.liveModeRenderStrategy = liveModeRenderStrategy;
	}

	public void setEditModeRenderStrategy(RenderStrategy editModeRenderStrategy) {
		this.editModeRenderStrategy = editModeRenderStrategy;
	}
	
	public void setRiotServletName(String riotServletName) {
		this.riotServletName = riotServletName;
	}
	
	public void setServletContext(ServletContext servletContext) {
		if (editModeRenderStrategy == null) {
			// UGLY HACK: Usually the editModeRenderStrategy is set when the 
			// riot-servlet is initialized. If the website-servlet is reloaded 
			// we have to look up the strategy again ...
			try {
				WebApplicationContext ctx = SpringUtils.getWebsiteApplicationContext(servletContext, riotServletName);
				this.editModeRenderStrategy = SpringUtils.beanOfType(ctx, EditModeRenderStrategy.class);
			}
			catch (NoSuchBeanDefinitionException e) {
			}
			catch (IllegalStateException e) {
			}
		}
	}
	

	private ComponentList createList(Content content, String key, 
			ComponentListConfig config) {
		
		final ComponentList list = new ComponentList();
		content.setValue(key, list);
		if (config.getInitialTypes() != null) {
			for (String type : config.getInitialTypes()) {
				Component component = new Component(type);
				component.wrap(config.getConfig(type).getDefaults());
				list.appendComponent(component);
			}
		}
		new TransactionTemplate(transactionManager).execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				list.save();
			}
		});
		return list;
	}
	
	public String renderComponentList(ContentContainer container, 
			String key, ComponentListConfig config,
			HttpServletRequest request,
			HttpServletResponse response) 
			throws Exception {

		ComponentList list = null;
		RenderStrategy strategy = liveModeRenderStrategy;
		if (EditModeUtils.isPreview(request, container)) {
			list = (ComponentList) container.getPreviewVersion().getValue(key);
			if (EditModeUtils.isEditMode(request)) {
				if (list == null) {
					list = createList(container.getPreviewVersion(), key, config);
					
					// If the new list is not empty, we have to store it and mark
					// the container as dirty.
					if (list.getSize() > 0) {
						container.getPreviewVersion().setValue(key, list);
						container.setDirty(true);
					}
				}
				if (AccessController.isGranted("edit", container)) {
					strategy = editModeRenderStrategy;
				}
			}
		}
		else if (container.getLiveVersion() != null) {
			list = (ComponentList) container.getLiveVersion().getValue(key);
		}
		
		StringWriter sw = new StringWriter();
		if (list != null) {
			strategy.render(list, config, request, new CapturingResponseWrapper(response, sw));
		}
		return sw.toString();
	}
	
	public String renderNestedComponentList(Component component, 
			String key, ComponentListConfig config,
			HttpServletRequest request,
			HttpServletResponse response) 
			throws Exception {

		ComponentList list;
		RenderStrategy strategy = liveModeRenderStrategy;
		request.setAttribute(PARENT_ATTRIBUTE, component);
		list = (ComponentList) component.getValue(key);
		if (EditModeUtils.isEditMode(request)) {
			if (list == null) {
				list = createList(component, key, config);
			}
			//TODO Pass the root container instead ...
			if (AccessController.isGranted("edit", list)) {
				strategy = editModeRenderStrategy;
			}
		}
		
		StringWriter sw = new StringWriter();
		if (list != null) {
			strategy.render(list, config, request, new CapturingResponseWrapper(response, sw));
		}
		request.removeAttribute(PARENT_ATTRIBUTE);
		return sw.toString();
	}

}

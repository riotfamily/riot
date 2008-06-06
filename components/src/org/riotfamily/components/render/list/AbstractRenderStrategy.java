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

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.components.config.ComponentListConfig;
import org.riotfamily.components.config.ComponentRepository;
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.ComponentList;
import org.riotfamily.components.render.component.ComponentRenderer;

public abstract class AbstractRenderStrategy implements RenderStrategy {
	
	public static final String INHERTING_COMPONENT = "inherit";
	
	protected Log log = LogFactory.getLog(getClass());
	
	protected ComponentDao dao; 
	
	protected ComponentRepository repository;
					
	public AbstractRenderStrategy(ComponentDao dao, 
			ComponentRepository repository) {
		
		this.dao = dao;
		this.repository = repository;
	}
		
	/**
	 * Renders the given list. The default implementation calls 
	 * {@link #getComponentsToRender(ComponentList)} and passes the result
	 * to {@link #renderComponents(List)}.
	 */
	public void render(ComponentList list,
			ComponentListConfig config,
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {
		
		renderComponents(list.getComponents(), config, request, response);
	}
	
	/**
	 * Renders the given list. The default implementation iterates over the 
	 * given list and calls {@link #renderContainer(Component, String)} 
	 * for each item. If the list is empty or null, 
	 * {@link #onEmptyComponentList(HttpServletRequest, HttpServletResponse)} is invoked.
	 */
	protected final void renderComponents(List<Component> components,
			ComponentListConfig config, 
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		if (components == null || components.isEmpty()) {
			onEmptyComponentList(config, request, response);
			return;
		}
		
		int i = 0;
		for (Component component : components) {
			renderComponent(component, i++, components.size(), config, 
					request, response);
		}
	}
	
	protected void onEmptyComponentList(ComponentListConfig config, 
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {

	}
	
	protected final void renderComponent(Component component,
			int position, int listSize, ComponentListConfig config, 
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {

		ComponentRenderer renderer = repository.getRenderer(component.getType());		
		renderComponent(renderer, component, position, listSize, config, request, response);
	}
	
	protected abstract void renderComponent(ComponentRenderer renderer, 
			Component component, int position, int listSize,
			ComponentListConfig config, 
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception;

	
	protected RenderStrategy getStrategyForParentList() throws IOException {
		return this;
	}
	
}

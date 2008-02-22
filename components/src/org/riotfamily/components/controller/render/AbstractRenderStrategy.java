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
package org.riotfamily.components.controller.render;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.components.config.ComponentListConfiguration;
import org.riotfamily.components.config.ComponentRepository;
import org.riotfamily.components.config.component.AbstractComponent;
import org.riotfamily.components.config.component.ComponentRenderer;
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.ComponentList;
import org.riotfamily.components.model.ComponentListLocation;

public abstract class AbstractRenderStrategy implements RenderStrategy {
	
	public static final String INHERTING_COMPONENT = "inherit";
	
	protected Log log = LogFactory.getLog(getClass());
	
	protected ComponentDao dao; 
	
	protected ComponentRepository repository;
				
	protected ComponentListConfiguration config;
	
	public AbstractRenderStrategy(ComponentDao dao, 
			ComponentRepository repository, ComponentListConfiguration config) {
		
		this.dao = dao;
		this.repository = repository;
		this.config = config;
	}
	
	public final void render(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		ComponentListLocation location = getLocation(request);
		render(location, request, response);
	}
	
	protected void render(ComponentListLocation location, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		ComponentList list = getComponentList(location, request);
		if (list != null) {
			renderComponentList(list, request, response);
		}
		else {
			onListNotFound(location, request, response);
		}
	}
	
	public void render(ComponentList list, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		renderComponentList(list, request, response);
	}
	
	protected final ComponentListLocation getLocation(HttpServletRequest request) {
		ComponentListLocation location = config.getLocator().getLocation(request);
		log.debug("List location: " + location);
		return location;
	}
	
	protected void onListNotFound(ComponentListLocation location, 
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {
		
		log.debug("No ComponentList found for " + location); 
	}
	
	protected Component getParentContainer(HttpServletRequest request) {
		return (Component) request.getAttribute(AbstractComponent.CONTAINER);
	}
	
	/**
	 * Returns the ComponentList to be rendered. The default implementation
	 * uses the controller's ComponentDao to look up a list for the given 
	 * location.
	 */
	protected ComponentList getComponentList(ComponentListLocation location, 
			HttpServletRequest request) {
		
		Component parent = getParentContainer(request);
		if (parent != null) {
			return dao.findComponentList(parent, location.getSlot());
		}
		return dao.findComponentList(location);
	}	
	
	/**
	 * Renders the given list. The default implementation calls 
	 * {@link #getComponentsToRender(ComponentList)} and passes the result
	 * to {@link #renderComponents(List)}.
	 */
	protected void renderComponentList(ComponentList list, 
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {
		
		List components = getComponentsToRender(list);
		renderComponents(components, request, response);
	}
	
	/**
	 * Renders the given list. The default implementation iterates over the 
	 * given list and calls {@link #renderContainer(Component, String)} 
	 * for each item. If the list is empty or null, 
	 * {@link #onEmptyComponentList(HttpServletRequest, HttpServletResponse)} is invoked.
	 */
	protected final void renderComponents(List components, 
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		if (components == null || components.isEmpty()) {
			onEmptyComponentList(request, response);
			return;
		}
		
		int i = 0;
		Iterator it = components.iterator();
		while (it.hasNext()) {
			Component component = (Component) it.next();
			renderComponent(component, i++, components.size(), request, response);
		}
	}
	
	protected void onEmptyComponentList(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {

	}
	
	/**
	 * Returns a list of VersionContainers. The default implementation
	 * simply returns the list's live components.
	 */
	protected List getComponentsToRender(ComponentList list) {
		return list.getLiveComponents();
	}
	
	protected final void renderComponent(Component component, 
			int position, int listSize, HttpServletRequest request, 
			HttpServletResponse response) throws Exception {

		if (INHERTING_COMPONENT.equals(component.getType())) {
			renderParentList(component.getList(), request, response);
		}
		else {
			ComponentRenderer renderer = repository.getComponent(component.getType());		
			renderComponent(renderer, component, position, listSize, 
					request, response);
		}
	}
	
	protected abstract void renderComponent(ComponentRenderer renderer, 
			Component component, int position, int listSize, 
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception;
	
	
	protected final void renderParentList(ComponentList list, 
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		ComponentListLocation location = list.getLocation();
		ComponentListLocation parentLocation = config.getLocator().getParentLocation(location);
		log.debug("Parent location: " + parentLocation);
		
		if (parentLocation != null) {
			if (location.equals(parentLocation)) {
				log.warn("Parent location is the same");
				return;
			}
			ComponentList parentList = getComponentList(parentLocation, request);
			if (parentList != null) {
				getStrategyForParentList().render(parentList, request, response);
			}
			else {
				onListNotFound(parentLocation, request, response);
			}
		}
	}
	
	protected RenderStrategy getStrategyForParentList() throws IOException {
		return this;
	}

}

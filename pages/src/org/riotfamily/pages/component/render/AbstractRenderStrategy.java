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
package org.riotfamily.pages.component.render;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.pages.component.Component;
import org.riotfamily.pages.component.ComponentList;
import org.riotfamily.pages.component.ComponentRepository;
import org.riotfamily.pages.component.ComponentVersion;
import org.riotfamily.pages.component.VersionContainer;
import org.riotfamily.pages.component.config.ComponentListConfiguration;
import org.riotfamily.pages.component.dao.ComponentDao;
import org.riotfamily.pages.component.impl.AbstractComponent;

public class AbstractRenderStrategy implements RenderStrategy {
	
	public static final String INHERTING_COMPONENT = "inherit";
	
	protected Log log = LogFactory.getLog(getClass());
	
	protected ComponentDao dao; 
	
	protected ComponentRepository repository;
				
	protected ComponentListConfiguration config;
	
	protected HttpServletRequest request;
	
	protected HttpServletResponse response;
	
	protected PrintWriter out;
	
	
	public AbstractRenderStrategy(ComponentDao dao, 
			ComponentRepository repository, ComponentListConfiguration config,
			HttpServletRequest request, HttpServletResponse response) 
			throws IOException {
		
		this.dao = dao;
		this.repository = repository;
		this.config = config;
		this.request = request;
		this.response = response;
		this.out = response.getWriter();
	}
	
	public final void render() throws IOException {
		render(getComponentPath(), getComponentKey());
	}
	
	protected void render(String path, String key) throws IOException {
		ComponentList list = getComponentList(path, key);
		if (list != null) {
			renderComponentList(list);
		}
		else {
			onListNotFound(path, key);
		}
	}
	
	public void render(ComponentList list) throws IOException {
		renderComponentList(list);
	}
	
	protected String getComponentPath() {
		VersionContainer parentContainer = (VersionContainer) 
				request.getAttribute(AbstractComponent.CONTAINER);
		
		if (parentContainer != null) {
			return parentContainer.getList().getPath();
		}
		return config.getComponentPathResolver().getComponentPath(request);
	}
	
	protected String getComponentKey() {
		VersionContainer parentContainer = (VersionContainer) 
				request.getAttribute(AbstractComponent.CONTAINER);
		
		if (parentContainer != null) {
			return parentContainer.getList().getKey() + "$" 
					+ parentContainer.getId();
		}
		return config.getComponentKeyResolver().getComponentKey(request);
	}
	

	protected void onListNotFound(String path, String key) throws IOException {
		log.debug("No ComponentList found with path " 
					+ path + " and key " + key);
	}
	
	/**
	 * Returns the ComponentList to be rendered. The default implementation
	 * uses the controller's ComponentDao to look up a list for the current 
	 * path/key-combination.
	 */
	protected ComponentList getComponentList(String path, String key) {
		log.debug("Looking up ComponentList " + path + '#' + key);
		return dao.findComponentList(path, key);
	}	
	
	/**
	 * Renders the given list. The default implementation calls 
	 * {@link #getComponentsToRender(ComponentList)} and passes the result
	 * to {@link #renderComponents(List)}.
	 */
	protected void renderComponentList(ComponentList list) throws IOException {
		List components = getComponentsToRender(list);
		renderComponents(components);
	}
	
	/**
	 * Renders the given list. The default implementation iterates over the 
	 * given list and calls {@link #renderContainer(VersionContainer)} for each
	 * item. If the list is empty or null, {@link #onEmptyComponentList()} is
	 * invoked.
	 */
	protected final void renderComponents(List components) throws IOException {
		if (components == null || components.isEmpty()) {
			onEmptyComponentList();
			return;
		}
		
		int i = 0;
		Iterator it = components.iterator();
		while (it.hasNext()) {
			VersionContainer container = (VersionContainer) it.next();
			renderContainer(container, getPositionalClassName(i++, !it.hasNext()));
		}
	}
	
	protected void onEmptyComponentList() throws IOException {
	}
	
	/**
	 * Returns a list of VersionContainers. The default implementation
	 * simply returns the list's live components.
	 */
	protected List getComponentsToRender(ComponentList list) {
		return list.getLiveList();
	}
	
	/**
	 * Renders the given VersionContainer. The default implementation calls 
	 * {@link #getVersionToRender(VersionContainer) getVersionToRender()} and 
	 * passes the result to {@link #renderComponentVersion(ComponentVersion)
	 * renderComponentVersion()} (if not null). 
	 */
	
	protected void renderContainer(VersionContainer container, 
			String positionClassName) throws IOException {

		ComponentVersion version = getVersionToRender(container);
		if (version != null) {
			renderComponentVersion(version, positionClassName);
		}
	}
		
	/**
	 * Returns the ComponentVersion to render. The default implementation 
	 * simply returns the component's live version.
	 */
	protected ComponentVersion getVersionToRender(VersionContainer container) {
		return container.getLiveVersion();
	}
	
	/**
	 * Renders the given ComponentVersion. 
	 * @throws IOException 
	 */
	protected final void renderComponentVersion(ComponentVersion version, 
			String positionClassName) throws IOException {
		
		String type = version.getType();
		if (INHERTING_COMPONENT.equals(type)) {
			renderParentList(version.getContainer().getList());
		}
		else {
			Component component = repository.getComponent(type);		
			renderComponent(component, version, positionClassName);
		}
	}
	
	protected final void renderParentList(ComponentList list) 
			throws IOException {
		
		String path = list.getPath();
		log.debug("Path: " + path);
		
		String parentPath = config.getComponentPathResolver().getParentPath(path);
		log.debug("Parent path: " + parentPath);
		
		if (parentPath != null) {
			if (path.equals(parentPath)) {
				log.warn("Parent path is the same");
				return;
			}
			ComponentList parentList = getComponentList(parentPath, list.getKey());
			if (parentList != null) {
				getStrategyForParentList().render(parentList);
			}
			else {
				onListNotFound(parentPath, list.getKey());
			}
		}
	}
	
	protected RenderStrategy getStrategyForParentList() throws IOException {
		return this;
	}
	
	protected void renderComponent(Component component, 
			ComponentVersion version, String positionClassName) 
			throws IOException {
		
		component.render(version, positionClassName, request, response);
	}
	
	protected String getPositionalClassName(int position, boolean last) {
		StringBuffer sb = new StringBuffer("component-").append(position + 1);
		if (last) {
			sb.append(" last-component");
		}
		return sb.toString();
	}

}

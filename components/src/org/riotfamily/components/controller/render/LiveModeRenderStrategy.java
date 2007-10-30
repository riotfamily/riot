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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.CacheService;
import org.riotfamily.cachius.CacheableRequestProcessor;
import org.riotfamily.cachius.CachiusResponseWrapper;
import org.riotfamily.cachius.TaggingContext;
import org.riotfamily.components.config.ComponentListConfiguration;
import org.riotfamily.components.config.ComponentRepository;
import org.riotfamily.components.config.component.Component;
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.components.model.ComponentList;
import org.riotfamily.components.model.ComponentVersion;
import org.riotfamily.components.model.Location;
import org.riotfamily.components.model.VersionContainer;

public class LiveModeRenderStrategy extends AbstractRenderStrategy {

	private CacheService cacheService;

	public LiveModeRenderStrategy(ComponentDao dao,
			ComponentRepository repository, ComponentListConfiguration config,
			CacheService cacheService) {

		super(dao, repository, config);
		this.cacheService = cacheService;
	}

	protected boolean isCacheable(Location location, 
			HttpServletRequest request) {
		
		String cacheKey = location.toString();
		if (cacheService.isCached(cacheKey)) {
			return true;
		}
		ComponentList list = getComponentList(location, request);
		if (list != null) {
			List containers = getComponentsToRender(list);
			if (containers != null) {
				Iterator it = containers.iterator();
				while (it.hasNext()) {
					VersionContainer container = (VersionContainer) it.next();
					ComponentVersion version = getVersionToRender(container);
					if (!INHERTING_COMPONENT.equals(version.getType())) {
						if (repository.getComponent(version).isDynamic()) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * Overrides the default implementation to render the cached version of
	 * the list (if present).
	 * @param request, 
	 */
	public void render(Location location, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		if (isCacheable(location, request)) {
			cacheService.serve(request, response, new ListProcessor(location));
		}
		else {
			renderInternal(location, request, response);
		}
	}
	
	protected void renderInternal(Location location, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		super.render(location, request, response);
	}
	
	protected void renderComponent(Component component,
			ComponentVersion version, String positionClassName, 
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		if (component.isDynamic() || response instanceof CachiusResponseWrapper) {
			renderComponentInternal(component, version, positionClassName, 
					request, response);
		}
		else {
			cacheService.serve(request, response, new ComponentProcessor(
					component, version, positionClassName));
		}
	}
	
	protected void renderComponentInternal(Component component,
			ComponentVersion version, String positionClassName, 
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {
		
		super.renderComponent(component, version, positionClassName, 
				request, response);
	}

	private class ListProcessor implements CacheableRequestProcessor {
		
		private Location location;
		
		public ListProcessor(Location location) {
			this.location = location;
		}

		public String getCacheKey(HttpServletRequest request) {
			StringBuffer sb = new StringBuffer("ComponentList ");
			VersionContainer parent = getParentContainer(request);
			if (parent != null) {
				sb.append(parent.getId()).append('$');
				sb.append(location.getSlot());
			}
			else {
				sb.append(location);
			}
			return sb.toString();
		}
		
		public long getTimeToLive() {
			return -1;
		}
		
		public long getLastModified(HttpServletRequest request) {
			return 0;
		}
		
		public void processRequest(HttpServletRequest request, 
				HttpServletResponse response) throws Exception {
			
			TaggingContext.tag(request, location.toString());
			renderInternal(location, request, response);
		}
		
	}
	
	private class ComponentProcessor implements CacheableRequestProcessor {
		
		private Component component;
		
		private ComponentVersion version;
		
		private String positionClassName;
		
		public ComponentProcessor(Component component, ComponentVersion version, 
				String positionClassName) {
			
			this.component = component;
			this.version = version;
			this.positionClassName = positionClassName;
		}

		public String getCacheKey(HttpServletRequest request) {
			StringBuffer key = new StringBuffer();
			key.append(version.getClass().getName());
			key.append('#');
			key.append(version.getId());
			return key.toString();
		}
		
		public long getTimeToLive() {
			return -1;
		}
		
		public long getLastModified(HttpServletRequest request) {
			return 0;
		}
		
		public void processRequest(HttpServletRequest request, 
				HttpServletResponse response) throws Exception {
			
			Collection tags = component.getCacheTags(version);
			if (tags != null) {
				Iterator it = tags.iterator();
				while (it.hasNext()) {
					String tag = (String) it.next();
					TaggingContext.tag(request, tag);
				}
			}
			
			renderComponentInternal(component, version, positionClassName, 
					request, response);
		}
		
	}
}

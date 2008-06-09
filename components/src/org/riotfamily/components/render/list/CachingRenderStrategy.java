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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.CacheService;
import org.riotfamily.cachius.CacheableRequestProcessor;
import org.riotfamily.cachius.CachiusResponseWrapper;
import org.riotfamily.components.cache.ComponentCacheUtils;
import org.riotfamily.components.config.ComponentListConfig;
import org.riotfamily.components.config.ComponentRepository;
import org.riotfamily.components.context.ComponentRequestUtils;
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.ComponentList;
import org.riotfamily.components.render.component.ComponentRenderer;

public abstract class CachingRenderStrategy extends AbstractRenderStrategy {

	protected CacheService cacheService;

	public CachingRenderStrategy(ComponentDao dao,
			ComponentRepository repository,	CacheService cacheService) {

		super(dao, repository);
		this.cacheService = cacheService;
	}

	protected abstract boolean isPreview();
	
	protected void appendModeToCacheKey(StringBuffer cacheKey) {
		if (isPreview()) {
			cacheKey.append("preview:");
		}
		else {
			cacheKey.append("live:");
		}
	}
    
	protected String getCacheKey(HttpServletRequest request,
	    ComponentList list) {
        
	    StringBuffer key = new StringBuffer();
        appendModeToCacheKey(key);      
        key.append("ComponentList ");
        key.append(list.getId()); //REVIST
        if (ComponentRequestUtils.isComponentRequest(request)) {
            key.append("-partial"); 
        }
        return key.toString();
    }
    
	
	protected boolean isCacheable(ComponentList list,
			ComponentListConfig config, HttpServletRequest request) {
		
		String cacheKey = getCacheKey(request, list);
		if (cacheService.isCached(cacheKey)) {
			return true;
		}
		if (list != null) {
			List<Component> components = list.getComponents();
			for (Component component : components) {
				if (repository.getRenderer(component).isDynamic()) {
					return false;
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
	public void render(ComponentList list, 
			ComponentListConfig config,
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {
		
		if (isCacheable(list, config, request)) {
			ListProcessor processor = new ListProcessor(list, config);
			cacheService.serve(request, response, processor);
		}
		else {
			renderUncached(list, config, request, response);
		}
	}
	
	protected void renderUncached(ComponentList list, 
			ComponentListConfig config, 
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {
		
		ComponentCacheUtils.addContainerTags(request, list.getContainer(), isPreview());
		super.render(list, config, request, response);
	}
	
	protected final void renderComponent(ComponentRenderer renderer,
			Component component, int position, int listSize,
			ComponentListConfig config, 
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		if (renderer.isDynamic() || response instanceof CachiusResponseWrapper) {
			renderUncachedComponent(renderer, component, position, listSize,
					config, request, response);
		}
		else {
			cacheService.serve(request, response, new ComponentProcessor(
					renderer, component, position, listSize, config));
		}
	}
	
	private void renderUncachedComponent(ComponentRenderer renderer, 
			Component component, int position, int listSize,
			ComponentListConfig config, 
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {

		//REVISIT
		//ComponentCacheUtils.addContainerTags(request, component, isPreview());
		renderComponentInternal(renderer, component, position, listSize, 
				config, request, response);
	}
	
	protected void renderComponentInternal(ComponentRenderer renderer, 
			Component component, int position, int listSize,
			ComponentListConfig config, 
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {
		
		renderer.render(component, isPreview(), position, listSize, request, response);
	}
		
	private class ListProcessor implements CacheableRequestProcessor {
		
		private ComponentList list;
		
		private ComponentListConfig config;
		
		private ListProcessor(ComponentList list,
				ComponentListConfig config) {
			
			this.list = list;
			this.config = config;
		}

		public String getCacheKey(HttpServletRequest request) {
			return CachingRenderStrategy.this.getCacheKey(request, list);
		}
		
		public long getTimeToLive() {
			return -1;
		}
		
		public long getLastModified(HttpServletRequest request) {
			return 0;
		}
		
		public boolean responseShouldBeZipped(HttpServletRequest request) {
			return false;
		}
		
		public void processRequest(HttpServletRequest request, 
				HttpServletResponse response) throws Exception {
			
			renderUncached(list, config, request, response);
		}
		
	}
	
	private class ComponentProcessor implements CacheableRequestProcessor {
		
		private ComponentRenderer renderer;
		
		private Component component;
		
		private int position;
		
		private int listSize;
		
		private ComponentListConfig config; 
		
		public ComponentProcessor(ComponentRenderer renderer, Component component, 
				int position, int listSize, ComponentListConfig config) {
			
			this.renderer = renderer;
			this.component = component;
			this.position = position;
			this.listSize = listSize;
			this.config = config;
		}

		public String getCacheKey(HttpServletRequest request) {
			StringBuffer key = new StringBuffer();
			appendModeToCacheKey(key);
			key.append(component.getClass().getName());
			key.append('#');
			key.append(component.getId());
			return key.toString();
		}
		
		public long getTimeToLive() {
			return -1;
		}
		
		public long getLastModified(HttpServletRequest request) {
			return 0;
		}
		
		public boolean responseShouldBeZipped(HttpServletRequest request) {
			return false;
		}
		
		public void processRequest(HttpServletRequest request, 
				HttpServletResponse response) throws Exception {
			
			renderUncachedComponent(renderer, component, position, listSize, 
					config, request, response);
		}
		
	}
}

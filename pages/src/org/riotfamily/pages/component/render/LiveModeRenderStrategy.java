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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.CacheItem;
import org.riotfamily.cachius.CacheService;
import org.riotfamily.cachius.CacheableRequestProcessor;
import org.riotfamily.cachius.TaggingContext;
import org.riotfamily.pages.component.Component;
import org.riotfamily.pages.component.ComponentList;
import org.riotfamily.pages.component.ComponentRepository;
import org.riotfamily.pages.component.ComponentVersion;
import org.riotfamily.pages.component.VersionContainer;
import org.riotfamily.pages.component.config.ComponentListConfiguration;
import org.riotfamily.pages.component.dao.ComponentDao;
import org.springframework.util.Assert;


public class LiveModeRenderStrategy extends AbstractRenderStrategy {

	protected CacheService cacheService;
	
	protected String listTag;
	
	protected CacheItem cachedList;
	
	protected boolean cacheIndividualComponents = true;
	
	protected ComponentListCacheKeyProvider cacheKeyProvider;
	
	public LiveModeRenderStrategy(ComponentDao dao, 
			ComponentRepository repository, ComponentListConfiguration config,
			HttpServletRequest request, HttpServletResponse response, 
			CacheService cacheService) throws IOException {
		
		super(dao, repository, config, request, response);
		this.cacheService = cacheService;
		this.cacheKeyProvider = config.getCacheKeyProvider();
	}
	
	private boolean hasTimeToLive() {
		return getTimeToLive() != ComponentListConfiguration.NO_TIME_TO_LIVE;
	}
	
	private long getTimeToLive() {
		return config.getTimeToLive();
	}
	
	protected String getCacheKey(String path, String key) {
		return cacheKeyProvider.getComponentListCacheKey(
			request, parent, path, key);
	}
	
	protected void render(String path, String key) throws IOException {
		render(new ListHolder(path, key));
	}
	
	public void render(ComponentList list) throws IOException {
		render(new ListHolder(list));
	}
	
	private void render(ListHolder listHolder) throws IOException {
		String path = listHolder.getPath();
		String key = listHolder.getKey();
		listTag = getListTag(path, key);
		
		if (hasTimeToLive()) {
			log.debug("Serving component list " + path + "#" + key + ". " +
					"List has time-to-live. List will be cached as a whole " +
					"AND individual components may be cached.");
				
			cacheIndividualComponents = true;
			renderCached(listHolder);
		} else if (isCacheable(listHolder, config, request)) {
			log.debug("Serving component list " + path + "#" + key + ". " +
				"List has no dynamic components. " +
				"List will be cached as a whole.");
			
			cacheIndividualComponents = false;
			renderCached(listHolder);
		} else {
			log.debug("Serving component list " + path + "#" + key + ". " +
				"List has dynamic components. " +
				"List will NOT be cached as a whole, BUT individual " +
				"components may be cached.");

			cacheIndividualComponents = true;
			listHolder.render();
		}
	}
	
	protected boolean isCacheable(ListHolder listHolder,
		ComponentListConfiguration config, HttpServletRequest request) {
		
		String cacheKey = listHolder.getCacheKey();
		if (cacheService.isCached(cacheKey)) {
			return true;
		}
		
		ComponentList list = listHolder.getList();
		if (list != null) {
			List containers= getComponentsToRender(list);
			if (containers != null) {
				Iterator it = containers.iterator();
				while (it.hasNext()) {
					VersionContainer container = (VersionContainer) it.next();
					ComponentVersion version = getVersionToRender(container);
					if (INHERTING_COMPONENT.equals(version.getType())) {
					    return false;
					}
					else {
						Component component = repository.getComponent(version);
					    if (component.isDynamic()) {
					        return false;
					    }
					}
				}
			}
		}
		
		return true;
	}
	
	private void renderCached(ListHolder listHolder)
		throws IOException {
		
		try {
			cacheService.serve(request, response, new ListProcessor(listHolder));
		}
		catch (RuntimeException e) {
			throw e;
		}
		catch (IOException e) {
			throw e;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void renderComponent(Component component, 
		ComponentVersion version, String positionClassName) 
		throws IOException {
		
		tagCacheItems(component, version);
		if (!cacheIndividualComponents || component.isDynamic()) {
			log.debug("Rendering component of type " + version.getType() +
					". Component will NOT be cached.");
			
			super.renderComponent(component, version, positionClassName);
		}
		else {
			log.debug("Rendering component of type " + version.getType() + 
				". Component will be cached.");
			
			renderCacheableComponent(component, version, positionClassName);
		}
	}
	
	private void tagCacheItems(Component component, ComponentVersion version) {
		Collection tags = component.getCacheTags(version);
		if (tags != null) {
			Iterator it = tags.iterator();
			while (it.hasNext()) {
				String tag = (String) it.next();
				TaggingContext.tag(request, tag);
			}
		}
	}
	
	protected void renderCacheableComponent(Component component, 
		ComponentVersion version, String positionClassName) 
		throws IOException {
		
		try {
			cacheService.serve(request, response,
				new ComponentProcessor(component, version, positionClassName));
		}
		catch (RuntimeException e) {
			throw e;
		}
		catch (IOException e) {
			throw e;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	protected String getComponentCacheKey(ComponentVersion version) {
		return cacheKeyProvider.getComponentCacheKey(request, version);
	}
	
	private class ListHolder {
		private ComponentList list = null;
		private boolean listIsNull = false;
		private String path;
		private String key;
		
		public ListHolder(ComponentList list) {
			Assert.notNull(list);
			
			this.list = list;
			this.path = list.getPath();
			this.key = list.getKey();
			
			this.listIsNull = false;
		}

		public ListHolder(String path, String key) {
			this.path = path;
			this.key = key;
			
			this.listIsNull = false;
		}
		
		protected String getCacheKey() {
			return LiveModeRenderStrategy.this.getCacheKey(path, key);
		}

		public ComponentList getList() {
			if (list == null && !listIsNull) {
				log.debug("Loading component list " + path + "#" + key);
				this.list = getComponentList(path, key);
				this.listIsNull = (this.list == null);
			}
			
			return this.list;
		}

		public String getPath() {
			return this.path;
		}

		public String getKey() {
			return this.key;
		}

		public void render() throws IOException {
			if (list != null) {
				LiveModeRenderStrategy.super.render(list);
			} else {
				LiveModeRenderStrategy.super.render(path, key);
			}
		}
	}
	
	private class ListProcessor implements CacheableRequestProcessor {
		private ListHolder listHolder;

		public ListProcessor(ListHolder listHolder) {
			this.listHolder = listHolder;
		}

		public String getCacheKey(HttpServletRequest request) {
			return LiveModeRenderStrategy.this.getCacheKey(
				listHolder.getPath(), listHolder.getKey());
		}

		public long getLastModified(HttpServletRequest request) throws Exception {
			return hasTimeToLive()? System.currentTimeMillis(): 0;
		}

		public long getTimeToLive() {
			return LiveModeRenderStrategy.this.getTimeToLive();
		}

		public void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
			
			
			HttpServletRequest oldRequest = LiveModeRenderStrategy.this.request;
			HttpServletResponse oldResponse = LiveModeRenderStrategy.this.response;
			
			LiveModeRenderStrategy.this.request = request;
			LiveModeRenderStrategy.this.response = response;
			TaggingContext.tag(request, listTag);
			listHolder.render();
			LiveModeRenderStrategy.this.request = oldRequest;
			LiveModeRenderStrategy.this.response = oldResponse;
		}

		public boolean responseShouldBeZipped(HttpServletRequest request) {
			return false;
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
			return getComponentCacheKey(version);
		}

		public long getLastModified(HttpServletRequest request) throws Exception {
			return 0;
		}

		public long getTimeToLive() {
			return -1;
		}

		public void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
			
			log.debug("No cache item found, rendering component ...");
			TaggingContext.tag(request, listTag);
			component.render(version, positionClassName, request, response);
		}

		public boolean responseShouldBeZipped(HttpServletRequest request) {
			return false;
		}
	}
}

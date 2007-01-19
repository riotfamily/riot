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
package org.riotfamily.pages.component.render;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.Cache;
import org.riotfamily.cachius.CacheItem;
import org.riotfamily.cachius.CachiusResponseWrapper;
import org.riotfamily.cachius.ItemUpdater;
import org.riotfamily.cachius.spring.TaggingContext;
import org.riotfamily.cachius.support.SessionUtils;
import org.riotfamily.pages.component.Component;
import org.riotfamily.pages.component.ComponentList;
import org.riotfamily.pages.component.ComponentRepository;
import org.riotfamily.pages.component.ComponentVersion;
import org.riotfamily.pages.component.config.ComponentListConfiguration;
import org.riotfamily.pages.component.dao.ComponentDao;

public class LiveModeRenderStrategy extends AbstractRenderStrategy {

	protected Cache cache;
	
	protected String listTag;
	
	protected CacheItem cachedList;
	
	protected boolean listIsCacheable = true;
	
	public LiveModeRenderStrategy(ComponentDao dao, 
			ComponentRepository repository, ComponentListConfiguration config,
			HttpServletRequest request, HttpServletResponse response, 
			Cache cache) throws IOException {
		
		super(dao, repository, config, request, response);
		this.cache = cache;
	}
	
	/**
	 * Overrides the default implementation to render the cached version of
	 * the list (if present).
	 */
	public void render(String path, String key) throws IOException {
		String cacheKey = getCacheKey(path, key);
		cachedList = cache.getItem(cacheKey);
		if (cachedList != null && cachedList.exists() && !cachedList.isNew()) {
			log.debug("Serving cached list: " + path + '#' + key);
			cachedList.writeTo(request, response);
		}
		else {
			// List was invalidated or this is the 1st request
			super.render(path, key);
		}
	}
	
	public void render(ComponentList list) throws IOException {
		String path = list.getPath();
		String key = list.getKey();
		String cacheKey = getCacheKey(path, key);
		cachedList = cache.getItem(cacheKey);
		if (cachedList != null && cachedList.exists() && !cachedList.isNew()) {
			log.debug("Serving cached list: " + path + '#' + key);
			cachedList.writeTo(request, response);
		}
		else {
			// List was invalidated or this is the 1st request
			super.render(list);
		}
	}
	
	protected String getCacheKey(String path, String key) {
		StringBuffer sb = new StringBuffer();
		sb.append(path).append('#').append(key);
		SessionUtils.addStateToCacheKey(request, sb);
		return sb.toString();
	}
	
	protected void renderComponentList(ComponentList list) throws IOException {
		listTag = list.getPath() + ':' + list.getKey();
		try {
			TaggingContext.openNestedContext(request);
			TaggingContext.tag(request, listTag);
			
			ItemUpdater updater = new ItemUpdater(cachedList, request);
			response = new CachiusResponseWrapper(response, updater);
			
			super.renderComponentList(list);
			if (!listIsCacheable) {
				updater.discard();
				cachedList.delete();
			}
			
			response.flushBuffer();
			updater.updateCacheItem();
			
		}
		finally {
			cachedList.setTags(TaggingContext.popTags(request));
		}
	}

	protected void renderComponent(Component component, 
			ComponentVersion version, String positionClassName) 
			throws IOException {
		
		tagCacheItems(component, version);
		if (component.isDynamic()) {
			listIsCacheable = false;
			super.renderComponent(component, version, positionClassName);
		}
		else {
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
	
		String key = getComponentCacheKey(version);
		CacheItem cachedComponent = cache.getItem(key);
		if (cachedComponent.exists() && !cachedComponent.isNew()) {
			cachedComponent.writeTo(request, response);
			return;
		}
		try {
			TaggingContext.openNestedContext(request);
			TaggingContext.tag(request, listTag);
			
			ItemUpdater updater = new ItemUpdater(cachedComponent, request);
			CachiusResponseWrapper wrapper = new CachiusResponseWrapper(
					response, updater);
			
			component.render(version, positionClassName, request, wrapper);
			
			wrapper.flushBuffer();
			updater.updateCacheItem();
		}
		finally {
			cachedComponent.setTags(TaggingContext.popTags(request));
		}
	}
	
	protected String getComponentCacheKey(ComponentVersion version) {
		StringBuffer key = new StringBuffer();
		key.append(version.getClass().getName());
		key.append('#');
		key.append(version.getId());
		SessionUtils.addStateToCacheKey(request, key);
		return key.toString();
	}

}

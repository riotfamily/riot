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
package org.riotfamily.components.editor;

import java.io.StringWriter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.directwebremoting.Browser;
import org.directwebremoting.ScriptSession;
import org.directwebremoting.ScriptSessionFilter;
import org.directwebremoting.ScriptSessions;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.riotfamily.cachius.CacheService;
import org.riotfamily.common.servlet.CapturingResponseWrapper;
import org.riotfamily.common.util.Generics;
import org.riotfamily.common.util.RiotLog;
import org.riotfamily.components.cache.ComponentCacheUtils;
import org.riotfamily.components.config.ContentFormRepository;
import org.riotfamily.components.meta.ComponentMetaData;
import org.riotfamily.components.meta.ComponentMetaDataProvider;
import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.ComponentList;
import org.riotfamily.components.model.ContentContainer;
import org.riotfamily.components.model.ContentMap;
import org.riotfamily.components.render.component.ComponentRenderer;
import org.riotfamily.components.render.component.EditModeComponentRenderer;
import org.riotfamily.core.security.AccessController;
import org.riotfamily.core.security.auth.RiotUser;
import org.riotfamily.core.security.session.LoginManager;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Service bean to edit ComponentLists and ComponentVersions.
 */
@Transactional
@RemoteProxy(name="ComponentEditor")
public class ComponentEditorImpl implements ComponentEditor,
		MessageSourceAware {

	private RiotLog log = RiotLog.get(ComponentEditorImpl.class);

	private CacheService cacheService;
	
	private ComponentRenderer renderer;

	private ComponentMetaDataProvider metaDataProvider;
	
	private Map<String, Map<String, Object>> tinyMCEProfiles;

	private MessageSource messageSource;
	
	
	public ComponentEditorImpl(CacheService cacheService, 
			ComponentRenderer renderer, 
			ComponentMetaDataProvider metaDataProvider,
			ContentFormRepository formRepository) {
		
		this.cacheService = cacheService;
		this.renderer = new EditModeComponentRenderer(renderer, metaDataProvider, formRepository);
		this.metaDataProvider = metaDataProvider;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	@RemoteMethod
	public Map<String, Map<String, Object>> getTinyMCEProfiles() {
		return this.tinyMCEProfiles;
	}

	public void setTinyMCEProfiles(Map<String, Map<String, Object>> tinyMCEProfiles) {
		this.tinyMCEProfiles = tinyMCEProfiles;
	}

	/**
	 * Returns the value of the given property.
	 */
	@RemoteMethod
	public String getText(String contentId, String property) {
		ContentMap content = ContentMap.load(contentId);
		Object value = content.get(property);
		return value != null ? value.toString() : null;
	}

	/**
	 * Sets the given property to a new value.
	 */
	@RemoteMethod
	public void updateText(String contentId, String property, String text) {
		ContentMap content = ContentMap.load(contentId);
		content.put(property, text);
	}

	/**
	 *
	 */
	@RemoteMethod
	public String[] updateTextChunks(String componentId, String property,
			String[] chunks) {

		String[] html = new String[chunks.length];
		Component component = Component.load(componentId);
		component.put(property, chunks[0]);
		html[0] = renderComponent(component);
		
		String type = component.getType();
		ComponentList list = component.getList();
		int offset = list.indexOf(component);
		
		for (int i = 1; i < chunks.length; i++) {
			component = createComponent(list, type);
			list.add(offset + i, component);
			component.put(property, chunks[i]);
			html[i] = renderComponent(component);
		}
		return html;
	}
	
	@RemoteMethod
	public List<ComponentMetaData> getComponentMetaData(String[] types) {
		List<ComponentMetaData> result = Generics.newArrayList();
		for (int i = 0; i < types.length; i++) {
			result.add(metaDataProvider.getMetaData(types[i]));
		}
		return result;
	}
	
	/**
	 * Creates a new Component and inserts it in the list identified
	 * by the given id.
	 */
	@RemoteMethod
	public String insertComponent(String listId, int position, String type) {
		Assert.notNull(listId, "listId must not be null");
		Assert.notNull(type, "type must not be null");
		ComponentList componentList = ComponentList.load(listId);
		Component component = createComponent(componentList, type);
		componentList.add(position, component);
		return renderComponent(component);
	}

	/**
	 * Creates a new Component of the given type.
	 *
	 * @param type The type of the version to create
	 * @return The newly created component
	 */
	private Component createComponent(ComponentList list, String type) {
		Component component = new Component(list);
		component.setType(type);
		Map<String, Object> defaults = metaDataProvider.getMetaData(type).getDefaults();
		if (defaults != null) {
			component.putAll(defaults);
		}
		return component;
	}
	
	@RemoteMethod
	public String setType(String componentId, String type) {
		Assert.notNull(componentId, "componentId must not be null");
		Assert.notNull(type, "type must not be null");
		Component component = Component.load(componentId);
		component.setType(type);
		return renderComponent(component);
	}
	
	@RemoteMethod
	public String renderComponent(String componentId) {
		return renderComponent(Component.load(componentId));
	}

	private String renderComponent(Component component) {
		try {
			StringWriter sw = new StringWriter();
			HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
			HttpServletResponse response = getCapturingResponse(sw);
			renderer.render(component, request, response);
			return sw.toString();
		}
		catch (Exception e) {
			log.error("Error rendering component.", e);
			throw new RuntimeException(e);
		}
	}

	@RemoteMethod
	public void moveComponent(String componentId, String nextComponentId) {
		Component.load(componentId).move(nextComponentId);
	}

	@RemoteMethod
	public void deleteComponent(String componentId) {
		Component.load(componentId).delete();
	}
	
	@RemoteMethod
	public void markAsDirty(Long containerId) {
		ContentContainer container = ContentContainer.load(containerId);
		container.setDirty(true);
		ComponentCacheUtils.invalidatePreviewVersion(cacheService, container);
		nofifyUsers();
	}
	
	@RemoteMethod
	public void publish(Long[] containerIds) {
		if (containerIds != null) {
			for (Long id : containerIds) {
				if (id != null) {
					ContentContainer container = ContentContainer.load(id);
					container.publish();
				}
			}
		}
		nofifyUsers();
	}

	@RemoteMethod
	public void discard(Long[] containerIds) {
		if (containerIds != null) {
			for (Long id : containerIds) {
				if (id != null) {
					ContentContainer container = ContentContainer.load(id);
					container.discard();
				}
			}
		}
		nofifyUsers();
	}

	/**
	 * Performs a logout.
	 */
	@RemoteMethod
	public void logout() {
		WebContext ctx = WebContextFactory.get();
		LoginManager.logout(ctx.getHttpServletRequest(), 
				ctx.getHttpServletResponse());
	}

	/* Utility methods */
	
	private HttpServletResponse getCapturingResponse(StringWriter sw) {
		WebContext ctx = WebContextFactory.get();
		return new CapturingResponseWrapper(ctx.getHttpServletResponse(), sw);
	}

	private void nofifyUsers() {
		
		WebContext webContext = WebContextFactory.get();
		HttpServletRequest request = webContext.getHttpServletRequest();

		final ScriptSession currentSession = webContext.getScriptSession();
		final String host = request.getServerName();
		final RiotUser user = AccessController.getCurrentUser();
		
		Locale locale = RequestContextUtils.getLocale(request);

		currentSession.setAttribute("host", host);
		currentSession.setAttribute("userId", user.getUserId());
		
		String userName = "";
		if (user.getName() != null) {
			userName = " (" + user.getName() + ")";
		}
		
		final String message = messageSource.getMessage(
				"components.concurrentModification", 
				new Object[] {
					userName, "javascript:location.reload()" 
				},
				"The page has been modified by another user{0}. Please "
				+ "<a href=\"{1}\">reload</a> the page in order "
				+ "to see the changes.", locale);
		
		Browser.withAllSessionsFiltered(
			new ScriptSessionFilter() {
				public boolean match(ScriptSession session) {
					return !user.getUserId().equals(currentSession.getAttribute("userId")) 
							&& session.getPage().equals(currentSession.getPage())
							&& host.equals(currentSession.getAttribute("host"));
				}
			}, 
			new Runnable() {
				public void run() {
					ScriptSessions.addFunctionCall("riot.showNotification", message);
				}
			});
	}

}

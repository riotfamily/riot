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
package org.riotfamily.pages.component.editor;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.web.util.CapturingResponseWrapper;
import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.pages.component.ComponentList;
import org.riotfamily.pages.component.ComponentListController;
import org.riotfamily.pages.component.ComponentVersion;
import org.riotfamily.pages.component.VersionContainer;
import org.riotfamily.pages.component.config.ComponentListConfiguration;
import org.riotfamily.pages.component.context.PageRequestContext;
import org.riotfamily.pages.component.context.PageRequestUtils;
import org.riotfamily.pages.component.context.RequestContextExpiredException;
import org.riotfamily.pages.component.render.EditModeRenderStrategy;
import org.riotfamily.pages.component.render.LiveModeRenderStrategy;
import org.riotfamily.pages.setup.WebsiteConfigSupport;
import org.riotfamily.riot.security.LoginManager;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.util.Assert;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Service bean that is exposed via DWR and provides methods to edit
 * ComponentLists and ComponentVersions.
 */
public class ComponentEditorImpl extends WebsiteConfigSupport 
		implements ComponentEditor, MessageSourceAware {
	
	private Log log = LogFactory.getLog(ComponentEditorImpl.class);	
	
	private LoginManager loginManager;
	
	private MessageSource messageSource;
	
	private Map editorConfigs;	

	public void setLoginManager(LoginManager loginManager) {
		this.loginManager = loginManager;
	}
	
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	public Map getEditorConfigs() {
		return this.editorConfigs;
	}

	public void setEditorConfigs(Map editorConfigs) {
		this.editorConfigs = editorConfigs;
	}

	/**
	 * Returns the value of the given property.
	 */
	public String getText(Long containerId, String property) {
		VersionContainer container = getDao().loadVersionContainer(containerId);
		ComponentVersion version = getDao().getLatestVersion(container);
		return version.getProperty(property);
	}
	
	/**
	 * Sets the given property to a new value and returns the updated HTML.  
	 */
	public String updateText(String controllerId, Long containerId,
			String property, String text) 
			throws RequestContextExpiredException {
		
		VersionContainer container = getDao().loadVersionContainer(containerId);
		ComponentVersion version = getVersionToEdit(container, null);
		version.setProperty(property, text);
		getDao().updateComponentVersion(version);

		return getHtml(getConfig(controllerId), version);
	}
	
	/**
	 *   
	 */
	public ComponentInfo[] updateTextChunks(String controllerId, 
			Long containerId, String property, String[] chunks) 
			throws RequestContextExpiredException {
		
		ComponentInfo[] result = new ComponentInfo[chunks.length];
		
		VersionContainer container = getDao().loadVersionContainer(containerId);
		ComponentVersion version = getVersionToEdit(container, null);
		version.setProperty(property, chunks[0]);
		getDao().updateComponentVersion(version);
		
		ComponentInfo info = new ComponentInfo();
		info.setId(containerId);
		info.setType(version.getType());
		info.setHtml(getHtml(getConfig(controllerId), version));
		result[0] = info;

		ComponentList list = container.getList();
		
		int offset = getContainersToEdit(list).indexOf(container);
		
		for (int i = 1; i < chunks.length; i++) {
			info = insertComponent(controllerId, list.getId(), 
					offset + i, info.getType(), 
					Collections.singletonMap(property, chunks[i]));
			
			result[i] = info;
		}
		return result;
	}
	
	/**
	 * Returns a list of TypeInfo beans indicating which component types are
	 * valid for the given controller.   
	 */
	public List getValidTypes(String controllerId) {
		ComponentListConfiguration config = getConfig(controllerId);
		String[] types = config.getValidComponentTypes(); 
		Locale locale = getLocale();
		ArrayList result = new ArrayList();
		for (int i = 0; i < types.length; i++) {
			String id = types[i]; 
			String description = messageSource.getMessage("component." + id, 
					null, FormatUtils.xmlToTitleCase(id), locale);
			
			result.add(new TypeInfo(id, description));
		}
		return result;
	}
			
	/**
	 * Creates a new VersionContainer and inserts it in the list identified
	 * by the given id.
	 */
	public ComponentInfo insertComponent(String controllerId, Long listId, 
			int position, String type, Map properties) 
			throws RequestContextExpiredException {
		
		ComponentList componentList = getDao().loadComponentList(listId);
		List containers = getContainersToEdit(componentList);
		
		ComponentListConfiguration config = getConfig(controllerId);
		if (type == null) {
			String[] types = config.getValidComponentTypes();
			Assert.isTrue(types != null && types.length > 0,
					"At least one valid component type must be specified.");
			
			type = types[0];
		}
		
		VersionContainer container = getDao().createVersionContainer(
				type, properties, isInstantPublishMode());
		
		container.setList(componentList);
		if (position >= 0) {
			containers.add(position, container);
		}
		else {
			containers.add(container);
		}
		
		if (!isInstantPublishMode()) {
			componentList.setDirty(true);
		}
		getDao().updateComponentList(componentList);
		
		return new ComponentInfo(container.getId(), type, 
				getRepository().getFormId(type), 
				getHtml(config, getDao().getLatestVersion(container)));
	}
	
	
	public ComponentInfo setType(String controllerId, Long containerId, 
			String type) throws RequestContextExpiredException {
		
		VersionContainer container = getDao().loadVersionContainer(containerId);
		ComponentVersion version = getVersionToEdit(container, type);

		version.setType(type);
		getDao().updateComponentVersion(version);
		
		return new ComponentInfo(containerId, type,
				getRepository().getFormId(type),
				getHtml(getConfig(controllerId), version));
	}
	
	public String getHtml(String controllerId, Long containerId) 
			throws RequestContextExpiredException {
		
		VersionContainer container = getDao().loadVersionContainer(containerId);
		ComponentVersion version = getDao().getLatestVersion(container);
		return getHtml(getConfig(controllerId), version);
	}
		
	private String getHtml(ComponentListConfiguration config, 
			ComponentVersion version) throws RequestContextExpiredException {
		
		try {
			StringWriter sw = new StringWriter();
			HttpServletRequest request = getWrappedRequest();
			HttpServletResponse response = getCapturingResponse(sw);
			
			EditModeRenderStrategy strategy = new EditModeRenderStrategy(
					getDao(), getRepository(), config, request, response);
			
			strategy.renderComponentVersion(version);
			return sw.toString();
			
		}
		catch (RequestContextExpiredException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("Error rendering component.", e);
			throw new RuntimeException(e);
		}
	}
	
	public String getLiveListHtml(String controllerId, Long listId) {
		try {
			ComponentListConfiguration config = getConfig(controllerId);
			ComponentList componentList = getDao().loadComponentList(listId);
			
			StringWriter sw = new StringWriter();
			HttpServletRequest request = getWrappedRequest();
			HttpServletResponse response = getCapturingResponse(sw);
			
			LiveModeRenderStrategy strategy = new LiveModeRenderStrategy(
					getDao(), getRepository(), config, request, response,
					getCache());
			
			strategy.render(componentList);
			return sw.toString();
		}
		catch (Exception e) {
			log.error("Error rendering component.", e);
			throw new RuntimeException(e);
		}	
	}
	
	public String getPreviewListHtml(String controllerId, Long listId) {
		try {
			ComponentListConfiguration config = getConfig(controllerId);
			ComponentList componentList = getDao().loadComponentList(listId);
			
			StringWriter sw = new StringWriter();
			HttpServletRequest request = getWrappedRequest();
			HttpServletResponse response = getCapturingResponse(sw);
			
			EditModeRenderStrategy strategy = new EditModeRenderStrategy(
					getDao(), getRepository(), config, request, response);
			
			strategy.setRenderOuterDiv(false);
			strategy.render(componentList);
			return sw.toString();
		}
		catch (Exception e) {
			log.error("Error rendering component.", e);
			throw new RuntimeException(e);
		}	
	}
	
	public void moveComponent(Long containerId, Long nextContainerId) {
		VersionContainer container = getDao().loadVersionContainer(containerId);
		ComponentList componentList = container.getList();
		List containers = getContainersToEdit(componentList);
		containers.remove(container);
		if (nextContainerId != null) {
			for (int i = 0; i < containers.size(); i++) {
				VersionContainer c = (VersionContainer) containers.get(i);
				if (c.getId().equals(nextContainerId)) {
					containers.add(i, container);
					break;
				}
			}
		}
		else {
			containers.add(container);
		}
		if (!isInstantPublishMode()) {
			componentList.setDirty(true);
		}
		getDao().updateComponentList(componentList);
	}
	
	public void deleteComponent(Long containerId) {
		VersionContainer container = getDao().loadVersionContainer(containerId);
		ComponentList componentList = container.getList();
		List containers = getContainersToEdit(componentList);
		containers.remove(container);
		if (!isInstantPublishMode()) {
			componentList.setDirty(true);
		}
		getDao().updateComponentList(componentList);
		if (!componentList.getLiveList().contains(container)) {
			getDao().deleteVersionContainer(container);
		}
	}
		
	public List getDirtyListIds(Long[] listIds) {
		if (isInstantPublishMode()) {
			return null;
		}
		ArrayList result = new ArrayList(listIds.length);
		for (int i = 0; i < listIds.length; i++) {
			ComponentList componentList = getDao().loadComponentList(listIds[i]);
			if (componentList.isDirty()) {
				result.add(componentList.getId());
				continue;
			}
			List containers = componentList.getLiveList();
			Iterator it = containers.iterator();
			while (it.hasNext()) {
				VersionContainer container = (VersionContainer) it.next();
				if (container.getPreviewVersion() != null
						&& container.getPreviewVersion().isDirty()) {
					
					result.add(componentList.getId());
					break;
				}
			}
		}
		return result;
	}
	
	/**
	 * Published all changes made to the lists identified by the given IDs. 
	 */
	public void publishLists(Long[] listIds) {
		for (int i = 0; i < listIds.length; i++) {
			publishList(listIds[i]);
		}
	}
	
	/**
	 * Discards all changes made to the lists identified by the given IDs. 
	 */
	public void discardLists(Long[] listIds) {
		for (int i = 0; i < listIds.length; i++) {
			discardList(listIds[i]);
		}
	}
	
	/**
	 * Discards all changes made to the ComponentList identified by the 
	 * given ID.
	 */
	public void discardList(Long listId) {
		ComponentList componentList = getDao().loadComponentList(listId);
		getDao().discardList(componentList);
	}
	
	/**
	 * Discards all changes made to the list with the specified ID and returns
	 * the HTML of the live version. A new preview version is implicitly 
	 * created, which is why the method name might be a little bit confusing.
	 */
	public String discardListAndGetPreviewHtml(String controllerId, Long listId) {
		discardList(listId);
		return getPreviewListHtml(controllerId, listId);
	}

	/**
	 * Publishes the ComponentList identified by the given ID.
	 */
	public void publishList(Long listId) {
		ComponentList componentList = getDao().loadComponentList(listId);
		if (getDao().publishList(componentList)) {
			log.debug("Changes published for ComponentList " + listId);
			if (getCache() != null) {
				String tag = componentList.getPath() 
						+ ':' + componentList.getKey();
				
				log.debug("Invalidating items tagged as " + tag);
				getCache().invalidateTaggedItems(tag);
			}
		}
	}
		
	/**
	 * 
	 */
	public boolean isInstantPublishMode() {
		WebContext ctx = WebContextFactory.get();
		HttpServletRequest request = ctx.getHttpServletRequest();
		String path = ServletUtils.getPath(ctx.getCurrentPage());
		PageRequestContext context = PageRequestUtils.getContext(request, path);
		if (context == null) {
			return false;
		}
		Boolean mode = (Boolean) context.getAttributes().get(
				INSTANT_PUBLISH_ATTRIBUTE);
		
		return mode != null && mode.booleanValue();
	}
	
	/**
	 * This method is invoked by the Riot toolbar to inform the server that
	 * the specified URL is still being edited. 
	 */
	public void keepAlive() {
		WebContext ctx = WebContextFactory.get();
		HttpServletRequest request = ctx.getHttpServletRequest();
		String path = ServletUtils.getPath(ctx.getCurrentPage());
		PageRequestUtils.touchContext(request, path);
	}
	
	/**
	 * Performs a logout. If a <code>loginManager</code> has been set, the 
	 * call is delegated, otherwise the session is invalidated.
	 */
	public void logout() {
		WebContext ctx = WebContextFactory.get();
		if (loginManager != null) {
			loginManager.logout(ctx.getHttpServletRequest(), 
					ctx.getHttpServletResponse());
		}
		else {
			ctx.getHttpServletRequest().getSession().invalidate();
		}
	}
	
	/* Utility methods */
	
	private HttpServletRequest getWrappedRequest() 
			throws RequestContextExpiredException {
		
		WebContext ctx = WebContextFactory.get();
		HttpServletRequest request = ctx.getHttpServletRequest();
		String path = ServletUtils.getPath(ctx.getCurrentPage());
		return PageRequestUtils.wrapRequest(request, path);
	}
	
	private HttpServletResponse getCapturingResponse(StringWriter sw) {
		WebContext ctx = WebContextFactory.get();
		return new CapturingResponseWrapper(ctx.getHttpServletResponse(), sw);
	}
	
	private Locale getLocale() {
		WebContext ctx = WebContextFactory.get();
		HttpServletRequest request = ctx.getHttpServletRequest();
		return RequestContextUtils.getLocale(request);
	}
	
	private ComponentListConfiguration getConfig(String controllerId) {
		return (ComponentListController) getControllers().get(controllerId);
	}
	
	private List getContainersToEdit(ComponentList list) {
		if (isInstantPublishMode()) {
			return list.getLiveList();
		}
		return getDao().getOrCreatePreviewContainers(list);
	}
	
	private ComponentVersion getVersionToEdit(VersionContainer container, 
			String type) {
		
		if (isInstantPublishMode()) {
			return container.getLiveVersion();
		}
		return getDao().getOrCreatePreviewVersion(container, type);
	}
		
}

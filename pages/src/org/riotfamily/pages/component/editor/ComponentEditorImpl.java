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
import org.riotfamily.pages.component.Component;
import org.riotfamily.pages.component.ComponentList;
import org.riotfamily.pages.component.ComponentListController;
import org.riotfamily.pages.component.ComponentVersion;
import org.riotfamily.pages.component.VersionContainer;
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
	 * Returns the value of the given property.
	 */
	public String getText(Long containerId, String property) {
		log.debug("getText(" + containerId + ", " + property + ")");
		ComponentVersion version = getLatestVersion(containerId);
		return version.getProperty(property);
	}
	
	/**
	 * Sets the given property to a new value and returns the updated HTML.  
	 */
	public String updateText(String controllerId, Long containerId,
			String property, String text) 
			throws RequestContextExpiredException {
		
		log.debug("updateText(" + containerId + ", " + property + ", " + text + ")");
		ComponentVersion version = getPreviewVersion(containerId, null);
		version.setProperty(property, text);
		getDao().updateComponentVersion(version);

		return getHtml(controllerId, version);
	}
	
	/**
	 *   
	 */
	public ComponentInfo[] updateTextChunks(String controllerId, 
			Long containerId, String property, String[] chunks) 
			throws RequestContextExpiredException {
		
		ComponentInfo[] result = new ComponentInfo[chunks.length];
		
		ComponentVersion version = getPreviewVersion(containerId, null);
		
		version.setProperty(property, chunks[0]);
		getDao().updateComponentVersion(version);
		
		ComponentInfo info = new ComponentInfo();
		info.setId(containerId);
		info.setType(version.getType());
		info.setHtml(getHtml(controllerId, version));
		result[0] = info;

		VersionContainer container = version.getContainer();
		ComponentList list = container.getList();
		
		int offset = getPreviewContainers(list).indexOf(container);
		
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
		log.debug("getvalidTypes(" + controllerId + ")");
		ComponentListController controller = (ComponentListController) 
				getControllers().get(controllerId);
		
		WebContext ctx = WebContextFactory.get();
		HttpServletRequest request = ctx.getHttpServletRequest();
		Locale locale = RequestContextUtils.getLocale(request);
		
		String[] types = controller.getValidComponentTypes(); 
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
	 * by the given ID.
	 */
	public ComponentInfo insertComponent(String controllerId, Long listId, 
			int position, String type, Map properties) 
			throws RequestContextExpiredException {
		
		log.debug("insertComponent(" + controllerId + ", " + listId 
				+ ", " + position + ")");
		
		ComponentList componentList = getDao().loadComponentList(listId);
		
		List containers = getPreviewContainers(componentList);
		
		ComponentListController controller = (ComponentListController) 
				getControllers().get(controllerId);

		if (type == null) {
			String[] types = controller.getValidComponentTypes();
			Assert.isTrue(types != null && types.length > 0,
					"At least one valid component type must be specified.");
			
			type = types[0];
		}
		
		VersionContainer container = createVersionContainer(type, properties);
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
				getHtml(controller, getLatestVersion(container)));
	}
	
	
	public ComponentInfo setType(String controllerId, Long containerId, 
			String type) throws RequestContextExpiredException {
		
		ComponentVersion version = getPreviewVersion(containerId, type);
		version.setType(type);
		getDao().updateComponentVersion(version);
		
		return new ComponentInfo(containerId, type,
				getRepository().getFormId(type),
				getHtml(controllerId, version));
	}
	
	public String getHtml(String controllerId, Long containerId) 
			throws RequestContextExpiredException {
		
		log.debug("getHtml(" + containerId + ")");
		ComponentVersion version = getLatestVersion(containerId);
		return getHtml(controllerId, version);
	}
	
	protected String getHtml(String controllerId, ComponentVersion version) 
			throws RequestContextExpiredException {
		
		ComponentListController controller = (ComponentListController) 
				getControllers().get(controllerId);
		
		return getHtml(controller, version);
	}
	
	protected String getHtml(ComponentListController controller, 
			ComponentVersion version) throws RequestContextExpiredException {
		
		try {
			Component component = getRepository().getComponent(version.getType());
			
			StringWriter sw = new StringWriter();
			HttpServletRequest request = getWrappedRequest();
			HttpServletResponse response = getCapturingResponse(sw);
			
			request.setAttribute(EditModeRenderStrategy.EDIT_MODE_ATTRIBUTE, 
					Boolean.TRUE);
							
			component.render(version, "component-new last-component", 
					controller, request, response);
			
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
		log.debug("getLiveListHtml(" + controllerId + ',' + listId + ')');
		try {
			ComponentListController controller = (ComponentListController) 
					getControllers().get(controllerId);
			
			ComponentList componentList = getDao().loadComponentList(listId);
			
			StringWriter sw = new StringWriter();
			HttpServletRequest request = getWrappedRequest();
			HttpServletResponse response = getCapturingResponse(sw);
			
			LiveModeRenderStrategy strategy = new LiveModeRenderStrategy(
					controller, request, response);
			
			strategy.render(componentList);
			return sw.toString();
		}
		catch (Exception e) {
			log.error("Error rendering component.", e);
			throw new RuntimeException(e);
		}	
	}
	
	public String getPreviewListHtml(String controllerId, Long listId) {
		log.debug("getPreviewListHtml(" + controllerId + ',' + listId + ')');
		try {
			ComponentListController controller = (ComponentListController) 
					getControllers().get(controllerId);
			
			ComponentList componentList = getDao().loadComponentList(listId);
			
			StringWriter sw = new StringWriter();
			HttpServletRequest request = getWrappedRequest();
			HttpServletResponse response = getCapturingResponse(sw);
			
			EditModeRenderStrategy strategy = new EditModeRenderStrategy(
					controller, request, response);
			
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
		log.debug("moveComponent(" + containerId + ", " + nextContainerId + ")");
		VersionContainer container = getDao().loadVersionContainer(containerId);
		ComponentList componentList = container.getList();
		List containers = getPreviewContainers(componentList);
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
		log.debug("deleteComponent(" + containerId + ")");
		VersionContainer container = getDao().loadVersionContainer(containerId);
		ComponentList componentList = container.getList();
		List containers = getPreviewContainers(componentList);
		containers.remove(container);
		if (!isInstantPublishMode()) {
			componentList.setDirty(true);
		}
		getDao().updateComponentList(componentList);
		if (!componentList.getLiveList().contains(container)) {
			deleteVersionContainer(container);
		}
	}
		
	public List getDirtyListIds(Long[] listIds) {
		log.debug("getDirtyListIds(...)");
		if (isInstantPublishMode()) {
			return null;
		}
		ArrayList result = new ArrayList(listIds.length);
		for (int i = 0; i < listIds.length; i++) {
			ComponentList componentList = getDao().loadComponentList(listIds[i]);
			if (componentList.isDirty()) {
				log.debug("Dirty list: " + componentList.getId());
				result.add(componentList.getId());
				continue;
			}
			List containers = componentList.getLiveList();
			Iterator it = containers.iterator();
			while (it.hasNext()) {
				VersionContainer container = (VersionContainer) it.next();
				if (container.getPreviewVersion() != null
						&& container.getPreviewVersion().isDirty()) {
					
					log.debug("Dirty component in list: " + componentList.getId());
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
		log.debug("publishLists(...)");
		for (int i = 0; i < listIds.length; i++) {
			publishList(listIds[i]);
		}
	}
	
	/**
	 * Discards all changes made to the lists identified by the given IDs. 
	 */
	public void discardLists(Long[] listIds) {
		log.debug("discardLists(...)");
		for (int i = 0; i < listIds.length; i++) {
			discardList(listIds[i]);
		}
	}
	
	/**
	 * Discards all changes made to the ComponentList identified by the 
	 * given ID.
	 */
	public void discardList(Long listId) {
		log.debug("discardList(" + listId + ")");
		ComponentList componentList = getDao().loadComponentList(listId);
		List previewList = componentList.getPreviewList();
		List liveList = componentList.getLiveList();
		if (componentList.isDirty()) {
			componentList.setPreviewList(null);
			componentList.setDirty(false);
			Iterator it = previewList.iterator();
			while (it.hasNext()) {
				VersionContainer container = (VersionContainer) it.next();
				if (liveList == null || !liveList.contains(container)) {
					log.debug("Deleting orphaned VersionContainer: " 
							+ container.getId());
					
					deleteVersionContainer(container);
				}
			}
			getDao().updateComponentList(componentList);	
		}
		Iterator it = liveList.iterator();
		while (it.hasNext()) {
			VersionContainer container = (VersionContainer) it.next();
			ComponentVersion preview = container.getPreviewVersion();
			if (preview != null) {
				log.debug("Deleting previewVersion: " + preview.getId());
				container.setPreviewVersion(null);
				getDao().updateVersionContainer(container);
				deleteComponentVersion(preview);
			}
		}
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
	 * Publishes the ComponentList identified by the given id.
	 */
	public void publishList(Long listId) {
		log.debug("publishList(" + listId + ')');
		ComponentList componentList = getDao().loadComponentList(listId);
		boolean invalidateCache = false;
		if (componentList.isDirty()) {
			invalidateCache = true;
			List previewList = componentList.getPreviewList();
			List liveList = componentList.getLiveList();
			if (liveList == null) {
				liveList = new ArrayList();
			}
			else {
				Iterator it = liveList.iterator();
				while (it.hasNext()) {
					VersionContainer container = (VersionContainer) it.next();
					if (!previewList.contains(container)) {
						log.debug("Deleting VersionContainer "
								+ container.getId());
						
						deleteVersionContainer(container);
					}
				}
				liveList.clear();
			}
			liveList.addAll(previewList);
			previewList.clear();
			componentList.setDirty(false);
			getDao().updateComponentList(componentList);
		}
		
		// Publish changes made to existing components ...
		Iterator it = componentList.getLiveList().iterator();
		while (it.hasNext()) {
			invalidateCache |= publishContainer((VersionContainer) it.next());
		}
		
		if (invalidateCache) {
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
	 * Publishes the given VersionContainer.
	 */
	protected boolean publishContainer(VersionContainer container) {
		log.debug("publishComponent(" + container.getId() + ')');
		ComponentVersion preview = container.getPreviewVersion();
		if (preview != null) {
			log.debug("Setting previewVersion as liveVersion ...");
			ComponentVersion liveVersion = container.getLiveVersion();
			if (liveVersion != null) {
				deleteComponentVersion(liveVersion);
			}
			container.setLiveVersion(preview);
			container.setPreviewVersion(null);
			getDao().updateVersionContainer(container);
			return true;
		}
		return false;
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
	
	protected HttpServletRequest getWrappedRequest() 
			throws RequestContextExpiredException {
		
		WebContext ctx = WebContextFactory.get();
		HttpServletRequest request = ctx.getHttpServletRequest();
		String path = ServletUtils.getPath(ctx.getCurrentPage());
		return PageRequestUtils.wrapRequest(request, path);
	}
	
	protected HttpServletResponse getCapturingResponse(StringWriter sw) {
		WebContext ctx = WebContextFactory.get();
		return new CapturingResponseWrapper(ctx.getHttpServletResponse(), sw);
	}
	
	protected List getPreviewContainers(ComponentList list) {
		if (isInstantPublishMode()) {
			return list.getLiveList();
		}
		List previewContainers = list.getPreviewList(); 
		if (!list.isDirty()) {
			if (previewContainers == null) {
				previewContainers = new ArrayList();
			}
			else {
				previewContainers.clear();
			}
			List liveContainers = list.getLiveList();
			if (liveContainers != null) {
				previewContainers.addAll(liveContainers);
			}
			list.setPreviewList(previewContainers);
			list.setDirty(true);
		}
		return previewContainers;
	}
	
	protected ComponentVersion getLatestVersion(Long containerId) {
		return getLatestVersion(getDao().loadVersionContainer(containerId));
	}
	
	protected ComponentVersion getLatestVersion(VersionContainer container) {
		ComponentVersion version = container.getPreviewVersion();
		if (version == null) {
			version = container.getLiveVersion();
		}
		return version;
	}
	
	protected ComponentVersion getPreviewVersion(Long containerId, String type) {
		VersionContainer container = getDao().loadVersionContainer(containerId);
		if (isInstantPublishMode()) {
			return container.getLiveVersion();
		}
		ComponentVersion preview = container.getPreviewVersion();
		if (preview == null) {
			ComponentVersion live = container.getLiveVersion();
			if (type == null) {
				type = live.getType();
			}
			Component component = getRepository().getComponent(type);
			preview = getComponentHelper().
					cloneComponentVersion(component, live);
			container.setPreviewVersion(preview);
			getDao().updateVersionContainer(container);
		}
		return preview;
	}
	
	protected VersionContainer createVersionContainer(
			String type, Map properties) {
		
		VersionContainer container = new VersionContainer();
		ComponentVersion version = new ComponentVersion(type);
		version.setProperties(properties);
		if (isInstantPublishMode()) {
			container.setLiveVersion(version);
		}
		else {
			container.setPreviewVersion(version);
		}
		version.setContainer(container);
		getDao().saveVersionContainer(container);
		return container;
	}
	
	protected void deleteVersionContainer(VersionContainer container) {
		deleteComponentVersion(container.getLiveVersion());
		deleteComponentVersion(container.getPreviewVersion());
		getDao().deleteVersionContainer(container);
	}
	
	protected void deleteComponentVersion(ComponentVersion version) {
		if (version != null) {			
			getDao().deleteComponentVersion(version);
		}
	}
}

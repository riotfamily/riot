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
import java.util.ArrayList;
import java.util.Collections;
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
import org.riotfamily.components.EditModeUtils;
import org.riotfamily.components.config.ComponentListConfiguration;
import org.riotfamily.components.config.ComponentRepository;
import org.riotfamily.components.context.PageRequestUtils;
import org.riotfamily.components.context.RequestContextExpiredException;
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.components.model.ComponentList;
import org.riotfamily.components.model.ComponentVersion;
import org.riotfamily.components.model.VersionContainer;
import org.riotfamily.components.service.ComponentService;
import org.riotfamily.riot.security.AccessController;
import org.riotfamily.riot.security.session.LoginManager;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Service bean to edit ComponentLists and ComponentVersions.
 */
public class ComponentEditorImpl implements ComponentEditor, MessageSourceAware {

	private Log log = LogFactory.getLog(ComponentEditorImpl.class);

	private ComponentDao componentDao;
	
	private ComponentService componentService;

	private ComponentRepository repository;

	private MessageSource messageSource;

	private Map editorConfigs;

	private boolean instantPublish = false;


	public ComponentEditorImpl(ComponentDao componentDao,
			ComponentService componentService, 
			ComponentRepository repository) {
		
		this.componentDao = componentDao;
		this.componentService = componentService;
		this.repository = repository;
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

	public void setInstantPublish(boolean instantPublish) {
		this.instantPublish = instantPublish;
	}

	protected ComponentVersion getVersionToEdit(VersionContainer container) {
		return componentService.getOrCreateVersion(container, null, instantPublish);
	}

	protected ComponentVersion getVersionToDisplay(VersionContainer container) {
		return instantPublish
				? container.getLiveVersion()
				: container.getLatestVersion();
	}

	protected List getContainersToEdit(ComponentList componentList) {
		return instantPublish
				? componentList.getLiveContainers()
				: componentService.getOrCreatePreviewContainers(componentList);
	}

	/**
	 * Returns the value of the given property.
	 */
	public String getText(Long containerId, String property) {
		VersionContainer container = componentDao.loadVersionContainer(containerId);
		ComponentVersion version = getVersionToDisplay(container);
		return version.getProperty(property);
	}

	/**
	 * Sets the given property to a new value.
	 */
	public void updateText(Long containerId, String property, String text) {
		VersionContainer container = componentDao.loadVersionContainer(containerId);
		ComponentVersion version = getVersionToEdit(container);
		version.setProperty(property, text);
		componentService.updateComponentVersion(version);
	}

	/**
	 * Sets the given property to a new value and memorizes the information
	 * that the property contains a reference to a file.
	 */
	public void updateFile(Long containerId, String property, String path, 
			String fileStoreId) {

		VersionContainer container = componentDao.loadVersionContainer(containerId);
		ComponentVersion version = getVersionToEdit(container);
		version.setProperty(property, path);
		componentService.updateComponentVersion(version);
		componentDao.saveFileStorageInfo(version.getType(), property, fileStoreId);
	}
	
	/**
	 *
	 */
	public void updateTextChunks(Long containerId, String property,
			String[] chunks) {

		log.debug("Inserting chunks " + StringUtils.arrayToCommaDelimitedString(chunks));
		VersionContainer container = componentDao.loadVersionContainer(containerId);
		ComponentVersion version = getVersionToEdit(container);
		version.setProperty(property, chunks[0]);
		componentService.updateComponentVersion(version);

		ComponentList list = container.getList();
		int offset = getContainersToEdit(list).indexOf(container);
		for (int i = 1; i < chunks.length; i++) {
			insertComponent(list.getId(), offset + i, version.getType(),
					Collections.singletonMap(property, chunks[i]));
		}
	}

	/**
	 * Returns a list of TypeInfo beans indicating which component types are
	 * valid for the given controller.
	 */
	public List getValidTypes(String controllerId) {
		ComponentListConfiguration cfg = repository.getListConfiguration(controllerId);
		Assert.notNull(cfg, "No such controller: " + controllerId);
		String[] types = cfg.getValidComponentTypes();

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
	public Long insertComponent(Long listId, int position, String type,
			Map properties) {

		ComponentList componentList = componentDao.loadComponentList(listId);
		VersionContainer container = componentService.insertContainer(componentList, type,
				properties, position, instantPublish);

		return container.getId();
	}

	public void setType(Long containerId, String type) {
		VersionContainer container = componentDao.loadVersionContainer(containerId);
		ComponentVersion version = getVersionToEdit(container);
		version.setType(type);
		componentService.updateComponentVersion(version);
	}

	private String getHtml(String url, String key, boolean live)
			throws RequestContextExpiredException {

		try {
			StringWriter sw = new StringWriter();
			HttpServletRequest request = getWrappedRequest(key);
			HttpServletResponse response = getCapturingResponse(sw);
			EditModeUtils.include(request, response, url, live);
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

	public List getLiveListHtml(ListRef[] ref) throws RequestContextExpiredException {
		ArrayList html = new ArrayList();
		for (int i = 0; i < ref.length; i++) {
			html.add(getHtml(ref[i].getControllerId(), ref[i].getContextKey(), true));
		}
		return html;
	}

	//TODO Use ListRef as argument
	public String getPreviewListHtml(String controllerId, Long listId)
			throws RequestContextExpiredException {

		String contextKey = controllerId;
		if (listId != null) {
			contextKey = listId.toString();
		}
		return getHtml(controllerId, contextKey, false);
	}

	public void moveComponent(Long containerId, Long nextContainerId) {
		VersionContainer container = componentDao.loadVersionContainer(containerId);
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
		if (!instantPublish) {
			componentList.setDirty(true);
		}
		componentDao.updateComponentList(componentList);
	}

	public void deleteComponent(Long containerId) {
		VersionContainer container = componentDao.loadVersionContainer(containerId);
		ComponentList componentList = container.getList();
		List containers = getContainersToEdit(componentList);
		containers.remove(container);
		if (!instantPublish) {
			componentList.setDirty(true);
		}
		componentDao.updateComponentList(componentList);
		if (!componentList.getLiveContainers().contains(container)) {
			componentService.deleteVersionContainer(container);
		}
	}

	public void publish(Long[] listIds, Long[] containerIds) {
		if (listIds != null) {
			publishLists(listIds);
		}
		if (containerIds != null) {
			publishContainers(containerIds);
		}
	}

	public void discard(Long[] listIds, Long[] containerIds) {
		if (listIds != null) {
			discardLists(listIds);
		}
		if (containerIds != null) {
			discardContainers(containerIds);
		}
	}

	/**
	 * Discards all changes made to the VersionContainers identified by the
	 * given IDs.
	 */
	private void discardContainers(Long[] ids) {
		for (int i = 0; i < ids.length; i++) {
			VersionContainer container = componentDao.loadVersionContainer(ids[i]);
			componentService.discardContainer(container);
		}
	}

	/**
	 * Discards all changes made to the ComponentLists identified by the
	 * given IDs.
	 */
	private void discardLists(Long[] listIds) {
		for (int i = 0; i < listIds.length; i++) {
			ComponentList componentList = componentDao.loadComponentList(listIds[i]);
			componentService.discardList(componentList);
		}
	}

	/**
	 * Publishes all changes made to the VersionContainers identified by the
	 * given IDs.
	 */
	private void publishContainers(Long[] ids) {
		for (int i = 0; i < ids.length; i++) {
			VersionContainer container = componentDao.loadVersionContainer(ids[i]);
			componentService.publishContainer(container);
		}
	}

	/**
	 * Publishes the ComponentList identified by the given ID.
	 */
	public void publishLists(Long[] listIds) {
		for (int i = 0; i < listIds.length; i++) {
			ComponentList list = componentDao.loadComponentList(listIds[i]);
			if (AccessController.isGranted("publish", list.getLocation())) {
				componentService.publishList(list);
			}
			else {
				throw new RuntimeException(messageSource.getMessage(
					"components.error.publishNotGranted", null, getLocale()));
			}
		}
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
	 * Performs a logout.
	 */
	public void logout() {
		WebContext ctx = WebContextFactory.get();
		LoginManager.logout(ctx.getHttpServletRequest(), 
				ctx.getHttpServletResponse());
	}

	/* Utility methods */

	private HttpServletRequest getWrappedRequest(String key)
			throws RequestContextExpiredException {

		WebContext ctx = WebContextFactory.get();
		HttpServletRequest request = ctx.getHttpServletRequest();
		String path = ServletUtils.getPath(ctx.getCurrentPage());
		return PageRequestUtils.wrapRequest(request, path, key);
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

}

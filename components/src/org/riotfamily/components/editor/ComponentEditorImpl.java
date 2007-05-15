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
import org.riotfamily.cachius.Cache;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.web.util.CapturingResponseWrapper;
import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.components.ComponentList;
import org.riotfamily.components.ComponentRepository;
import org.riotfamily.components.ComponentVersion;
import org.riotfamily.components.VersionContainer;
import org.riotfamily.components.context.PageRequestUtils;
import org.riotfamily.components.context.RequestContextExpiredException;
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.riot.security.AccessController;
import org.riotfamily.riot.security.LoginManager;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Service bean to edit ComponentLists and ComponentVersions.
 */
public class ComponentEditorImpl implements ComponentEditor, MessageSourceAware {

	private Log log = LogFactory.getLog(ComponentEditorImpl.class);

	private ComponentDao dao;

	private ComponentRepository repository;

	private LoginManager loginManager;

	private MessageSource messageSource;

	private Map editorConfigs;

	private Cache cache;

	public ComponentEditorImpl(ComponentDao dao, ComponentRepository repository) {
		this.dao = dao;
		this.repository = repository;
	}

	public void setLoginManager(LoginManager loginManager) {
		this.loginManager = loginManager;
	}

	public void setCache(Cache cache) {
		this.cache = cache;
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
		VersionContainer container = dao.loadVersionContainer(containerId);
		ComponentVersion version = dao.getLatestVersion(container);
		return version.getProperty(property);
	}

	/**
	 * Sets the given property to a new value and returns the updated HTML.
	 */
	public void updateText(Long containerId, String property, String text) {
		VersionContainer container = dao.loadVersionContainer(containerId);
		ComponentVersion version = dao.getOrCreatePreviewVersion(container, null);
		version.setProperty(property, text);
		dao.updateComponentVersion(version);
	}

	/**
	 *
	 */
	public void updateTextChunks(Long containerId, String property,
			String[] chunks) {

		log.debug("Inserting chunks " + StringUtils.arrayToCommaDelimitedString(chunks));
		VersionContainer container = dao.loadVersionContainer(containerId);
		ComponentVersion version = dao.getOrCreatePreviewVersion(container, null);
		version.setProperty(property, chunks[0]);
		dao.updateComponentVersion(version);

		ComponentList list = container.getList();
		int offset = dao.getOrCreatePreviewContainers(list).indexOf(container);
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
		String[] types = repository.getListConfiguration(controllerId)
				.getValidComponentTypes();

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

		ComponentList componentList = dao.loadComponentList(listId);
		VersionContainer container = dao.insertContainer(componentList, type,
				properties, position, false);

		return container.getId();
	}

	public void setType(Long containerId, String type) {
		VersionContainer container = dao.loadVersionContainer(containerId);
		ComponentVersion version = dao.getOrCreatePreviewVersion(container, type);
		version.setType(type);
		dao.updateComponentVersion(version);
	}

	private String getHtml(String url, Object key, boolean live)
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

	public String getLiveListHtml(String controllerId, Long listId)
			throws RequestContextExpiredException {

		Object contextKey = listId;
		if (contextKey == null) {
			contextKey = controllerId;
		}
		return getHtml(controllerId, contextKey, true);
	}

	public String getPreviewListHtml(String controllerId, Long listId)
			throws RequestContextExpiredException {

		Object contextKey = listId;
		if (contextKey == null) {
			contextKey = controllerId;
		}
		return getHtml(controllerId, contextKey, false);
	}

	public void moveComponent(Long containerId, Long nextContainerId) {
		VersionContainer container = dao.loadVersionContainer(containerId);
		ComponentList componentList = container.getList();
		List containers = dao.getOrCreatePreviewContainers(componentList);
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
		componentList.setDirty(true);
		dao.updateComponentList(componentList);
	}

	public void deleteComponent(Long containerId) {
		VersionContainer container = dao.loadVersionContainer(containerId);
		ComponentList componentList = container.getList();
		List containers = dao.getOrCreatePreviewContainers(componentList);
		containers.remove(container);
		componentList.setDirty(true);
		dao.updateComponentList(componentList);
		if (!componentList.getLiveContainers().contains(container)) {
			dao.deleteVersionContainer(container);
		}
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
	 * Discards all changes made to the VersionContainers identified by the
	 * given IDs.
	 */
	public void discardContainers(Long[] ids) {
		for (int i = 0; i < ids.length; i++) {
			VersionContainer container = dao.loadVersionContainer(ids[i]);
			dao.discardContainer(container);
		}
	}

	/**
	 * Discards all changes made to the ComponentList identified by the
	 * given ID.
	 */
	public void discardList(Long listId) {
		ComponentList componentList = dao.loadComponentList(listId);
		dao.discardList(componentList);
	}

	/**
	 * Discards all changes made to the list with the specified ID and returns
	 * the HTML of the live version. A new preview version is implicitly
	 * created, which is why the method name might be a little bit confusing.
	 */
	public String discardListAndGetPreviewHtml(String controllerId, Long listId)
			throws RequestContextExpiredException {

		discardList(listId);
		return getPreviewListHtml(controllerId, listId);
	}

	/**
	 * Publishes all changes made to the VersionContainers identified by the
	 * given IDs.
	 */
	public void publishContainers(Long[] ids) {
		for (int i = 0; i < ids.length; i++) {
			VersionContainer container = dao.loadVersionContainer(ids[i]);
			dao.publishContainer(container);
		}
	}

	/**
	 * Publishes the ComponentList identified by the given ID.
	 */
	public void publishList(Long listId) {
		ComponentList componentList = dao.loadComponentList(listId);

		if (AccessController.isGranted("publish", componentList, null)) {
			if (dao.publishList(componentList)) {
				log.debug("Changes published for ComponentList " + listId);
				if (cache != null) {
					String tag = componentList.getLocation().toString();
					log.debug("Invalidating items tagged as " + tag);
					cache.invalidateTaggedItems(tag);
				}
			}
		} else {
			throw new RuntimeException(messageSource.getMessage(
				"components.error.publishNotGranted", null, getLocale()));
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

	private HttpServletRequest getWrappedRequest(Object key)
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

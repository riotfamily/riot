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

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.riotfamily.common.image.ImageCropper;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.PasswordGenerator;
import org.riotfamily.common.web.util.CapturingResponseWrapper;
import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.components.EditModeUtils;
import org.riotfamily.components.config.ComponentListConfiguration;
import org.riotfamily.components.config.ComponentRepository;
import org.riotfamily.components.config.component.ComponentRenderer;
import org.riotfamily.components.context.PageRequestUtils;
import org.riotfamily.components.context.RequestContextExpiredException;
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.ComponentList;
import org.riotfamily.components.model.Content;
import org.riotfamily.components.model.ContentContainer;
import org.riotfamily.media.dao.MediaDao;
import org.riotfamily.media.model.RiotFile;
import org.riotfamily.media.model.RiotImage;
import org.riotfamily.media.model.data.CroppedImageData;
import org.riotfamily.media.model.data.ImageData;
import org.riotfamily.riot.security.AccessController;
import org.riotfamily.riot.security.session.LoginManager;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Service bean to edit ComponentLists and ComponentVersions.
 */
public class ComponentEditorImpl implements ComponentEditor, MessageSourceAware {

	private Log log = LogFactory.getLog(ComponentEditorImpl.class);

	private ComponentDao componentDao;
	
	private MediaDao mediaDao;
	
	private ImageCropper imageCropper;
	
	private PasswordGenerator tokenGenerator = 
			new PasswordGenerator(16, true, true, true);

	private Set validTokens = Collections.synchronizedSet(new HashSet());
	
	private ComponentRepository repository;

	private MessageSource messageSource;

	private Map tinyMCEProfiles;

	public ComponentEditorImpl(ComponentDao componentDao,
			MediaDao mediaDao, ImageCropper imageCropper,
			ComponentRepository repository) {
		
		this.componentDao = componentDao;
		this.mediaDao = mediaDao;
		this.imageCropper = imageCropper;
		this.repository = repository;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	public Map getTinyMCEProfiles() {
		return this.tinyMCEProfiles;
	}

	public void setTinyMCEProfiles(Map tinyMCEProfiles) {
		this.tinyMCEProfiles = tinyMCEProfiles;
	}

	/**
	 * Returns the value of the given property.
	 */
	public String getText(Long containerId, String property) {
		ContentContainer container = componentDao.loadContentContainer(containerId);
		Object value = container.getValue(property, true);
		return value != null ? value.toString() : null;
	}

	/**
	 * Sets the given property to a new value.
	 */
	public void updateText(Long containerId, String property, String text) {
		ContentContainer container = componentDao.loadContentContainer(containerId);
		Content version = getOrCreatePreviewVersion(container);
		version.setValue(property, text);
		componentDao.saveOrUpdateContent(version);
	}

	public String cropImage(Long containerId, String property, Long imageId,
			int width, int height, int x, int y, int scaledWidth)
			throws IOException {
	
		ContentContainer container = componentDao.loadContentContainer(containerId);
		Content version = getOrCreatePreviewVersion(container);
		
		RiotImage original = (RiotImage) mediaDao.loadFile(imageId);
		RiotImage croppedImage = new RiotImage(new CroppedImageData(
				original, imageCropper, width, height, x, y, scaledWidth));
		
		version.setValue(property, croppedImage);
		componentDao.saveOrUpdateContent(version);
		return croppedImage.getUri();
	}
	
	public String updateImage(Long containerId, String property, Long imageId) {
	
		ContentContainer container = componentDao.loadContentContainer(containerId);
		Content version = getOrCreatePreviewVersion(container);
		
		RiotFile image = mediaDao.loadFile(imageId);
		version.setValue(property, image);
		componentDao.saveOrUpdateContent(version);
		return image.getUri();
	}
	
	public void discardImage(Long imageId) {
		RiotFile image = mediaDao.loadFile(imageId);
		mediaDao.deleteFile(image);
	}
	
	public String generateToken() {
		String token = tokenGenerator.generate();
		validTokens.add(token);
		log.debug("Generated token: " + token);
		return token;
	}
	
	boolean isValidToken(String token) {
		boolean valid = validTokens.contains(token);
		log.debug((valid ? "Valid" : "Invalid") + " token: " + token);
		return valid;
	}
	
	public void invalidateToken(String token) {
		validTokens.remove(token);
	}
	
	RiotImage storeImage(String token, MultipartFile multipartFile) 
			throws IOException {
		
		RiotImage image = new RiotImage(new ImageData(multipartFile));
		mediaDao.saveFile(image);
		return image;
	}

	/**
	 *
	 */
	public void updateTextChunks(Long componentId, String property,
			String[] chunks) {

		log.debug("Inserting chunks " + StringUtils.arrayToCommaDelimitedString(chunks));
		Component component = componentDao.loadComponent(componentId);
		Content version = getOrCreatePreviewVersion(component);
		version.setValue(property, chunks[0]);
		componentDao.saveOrUpdateContent(version);
		
		ComponentList list = component.getList();
		int offset = list.getOrCreatePreviewContainers().indexOf(component);
		for (int i = 1; i < chunks.length; i++) {
			insertComponent(list.getId(), offset + i, component.getType(),
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
		
		Component container = createVersionContainer(type, properties);
		componentList.insertContainer(container, position);
		componentDao.updateComponentList(componentList);
		return container.getId();
	}

	/**
	 * Creates a new container, containing a version of the given type.
	 *
	 * @param type The type of the version to create
	 * @param properties Properties of the version to create
	 * @return The newly created container
	 */
	private Component createVersionContainer(String type, Map properties) {
		Component container = new Component(type);
		Content version = new Content();
		ComponentRenderer component = repository.getComponent(type);
		Map values = new HashMap();
		if (component.getDefaults() != null) {
			values.putAll(component.getDefaults());
		}
		if (properties != null) {
			values.putAll(properties);
		}
		version.setValues(values);
		container.setPreviewVersion(version);
		componentDao.saveContentContainer(container);
		return container;
	}
	
	public void setType(Long containerId, String type) {
		Component component = componentDao.loadComponent(containerId);
		component.setType(type);
		//componentDao.updateVersionContainer(component);
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

	public void moveComponent(Long componentId, Long nextComponentId) {
		Component component = componentDao.loadComponent(componentId);
		ComponentList componentList = component.getList();
		List components = componentList.getOrCreatePreviewContainers();
		components.remove(component);
		if (nextComponentId != null) {
			for (int i = 0; i < components.size(); i++) {
				Component c = (Component) components.get(i);
				if (c.getId().equals(nextComponentId)) {
					components.add(i, component);
					break;
				}
			}
		}
		else {
			components.add(component);
		}
		componentList.setDirty(true);
		componentDao.updateComponentList(componentList);
	}

	public void deleteComponent(Long componentId) {
		Component component = componentDao.loadComponent(componentId);
		ComponentList componentList = component.getList();
		List components = componentList.getOrCreatePreviewContainers();
		components.remove(component);
		componentList.setDirty(true);
		componentDao.updateComponentList(componentList);
		if (!componentList.getLiveComponents().contains(component)) {
			componentDao.deleteContentContainer(component);
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
			ContentContainer container = componentDao.loadContentContainer(ids[i]);
			componentDao.discardContainer(container);
		}
	}

	/**
	 * Discards all changes made to the ComponentLists identified by the
	 * given IDs.
	 */
	private void discardLists(Long[] listIds) {
		for (int i = 0; i < listIds.length; i++) {
			ComponentList componentList = componentDao.loadComponentList(listIds[i]);
			componentDao.discardComponentList(componentList);
		}
	}

	/**
	 * Publishes all changes made to the VersionContainers identified by the
	 * given IDs.
	 */
	private void publishContainers(Long[] ids) {
		for (int i = 0; i < ids.length; i++) {
			ContentContainer container = componentDao.loadContentContainer(ids[i]);
			componentDao.publishContainer(container);
		}
	}

	/**
	 * Publishes the ComponentList identified by the given ID.
	 */
	private void publishLists(Long[] listIds) {
		for (int i = 0; i < listIds.length; i++) {
			ComponentList list = componentDao.loadComponentList(listIds[i]);
			if (AccessController.isGranted("publish", list.getLocation())) {
				componentDao.publishComponentList(list);
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
	
	private Content getOrCreatePreviewVersion(ContentContainer container) {
		if (container instanceof Component) {
			Component component = (Component) container;
			ComponentList list = component.getList();
			if (list != null && !list.isDirty()) {
				list.getOrCreatePreviewContainers();
				componentDao.updateComponentList(list);
			}
		}
		Content previewVersion = container.getPreviewVersion();
		if (previewVersion == null) {
			Content liveVersion = container.getLiveVersion();
			if (liveVersion != null) {
				previewVersion = new Content(liveVersion);
			}
			else {
				previewVersion = new Content();
			}
			container.setPreviewVersion(previewVersion);
			componentDao.updateContentContainer(container);
		}
		return previewVersion;
	}

}

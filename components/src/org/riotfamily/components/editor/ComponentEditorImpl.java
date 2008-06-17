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
import org.riotfamily.components.config.ComponentListConfig;
import org.riotfamily.components.config.ComponentRepository;
import org.riotfamily.components.context.ComponentRequestUtils;
import org.riotfamily.components.context.RequestContextExpiredException;
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.ComponentList;
import org.riotfamily.components.model.Content;
import org.riotfamily.components.model.ContentContainer;
import org.riotfamily.components.render.component.ComponentRenderer;
import org.riotfamily.components.render.component.EditModeComponentRenderer;
import org.riotfamily.media.dao.MediaDao;
import org.riotfamily.media.model.RiotFile;
import org.riotfamily.media.model.RiotImage;
import org.riotfamily.media.model.data.CroppedImageData;
import org.riotfamily.media.model.data.ImageData;
import org.riotfamily.riot.security.session.LoginManager;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * Service bean to edit ComponentLists and ComponentVersions.
 */
@Transactional
public class ComponentEditorImpl implements ComponentEditor, UploadManager, 
		MessageSourceAware {

	private Log log = LogFactory.getLog(ComponentEditorImpl.class);

	private ComponentDao componentDao;
	
	private MediaDao mediaDao;
	
	private ImageCropper imageCropper;
	
	private PasswordGenerator tokenGenerator = 
			new PasswordGenerator(16, true, true, true);

	private Set<String> validTokens = Collections.synchronizedSet(new HashSet<String>());
	
	private ComponentRepository repository;

	private MessageSource messageSource;

	private Map<String, Map<String, Object>> tinyMCEProfiles;

	private EditModeComponentRenderer editModeRenderer;
	
	public ComponentEditorImpl(ComponentDao componentDao,
			MediaDao mediaDao, ImageCropper imageCropper,
			ComponentRepository repository) {
		
		this.componentDao = componentDao;
		this.mediaDao = mediaDao;
		this.imageCropper = imageCropper;
		this.repository = repository;
		editModeRenderer = new EditModeComponentRenderer(repository);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	public Map<String, Map<String, Object>> getTinyMCEProfiles() {
		return this.tinyMCEProfiles;
	}

	public void setTinyMCEProfiles(Map<String, Map<String, Object>> tinyMCEProfiles) {
		this.tinyMCEProfiles = tinyMCEProfiles;
	}

	/**
	 * Returns the value of the given property.
	 */
	public String getText(Long contentId, String property) {
		Content content = componentDao.loadContent(contentId);
		Object value = content.getValue(property);
		return value != null ? value.toString() : null;
	}

	/**
	 * Sets the given property to a new value.
	 */
	public void updateText(Long contentId, String property, String text) {
		Content content = componentDao.loadContent(contentId);
		content.setValue(property, text);
	}

	public String cropImage(Long contentId, String property, Long imageId,
			int width, int height, int x, int y, int scaledWidth)
			throws IOException {
	
		Content content = componentDao.loadContent(contentId);
		RiotImage original = (RiotImage) mediaDao.loadFile(imageId);
		RiotImage croppedImage = new RiotImage(new CroppedImageData(
				original, imageCropper, width, height, x, y, scaledWidth));
		
		content.setValue(property, croppedImage);
		//componentDao.saveOrUpdatePreviewVersion(container);
		return croppedImage.getUri();
	}
	
	public String updateImage(Long contentId, String property, Long imageId) {
		Content content = componentDao.loadContent(contentId);
		RiotFile image = mediaDao.loadFile(imageId);
		content.setValue(property, image);
		//componentDao.saveOrUpdatePreviewVersion(container);
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
	
	public boolean isValidToken(String token) {
		boolean valid = validTokens.contains(token);
		log.debug((valid ? "Valid" : "Invalid") + " token: " + token);
		return valid;
	}
	
	public void invalidateToken(String token) {
		validTokens.remove(token);
	}
	
	public RiotImage storeImage(String token, MultipartFile multipartFile) 
			throws IOException {
		
		RiotImage image = new RiotImage(new ImageData(multipartFile));
		mediaDao.saveFile(image);
		return image;
	}

	/**
	 *
	 */
	public String[] updateTextChunks(Long componentId, String property,
			String[] chunks) throws RequestContextExpiredException {

		String[] html = new String[chunks.length];
		Component component = componentDao.loadComponent(componentId);
		component.setValue(property, chunks[0]);
		html[0] = renderComponent(component);
		
		ComponentList list = component.getList();
		int offset = list.indexOf(component);
		for (int i = 1; i < chunks.length; i++) {
			html[i] = insertComponent(list.getId(), offset + i, 
					component.getType(),
					Collections.singletonMap(property, chunks[i]));
		}
		return html;
	}

	/**
	 * Returns a list of TypeInfo beans indicating which component types are
	 * valid for the list with the given id.
	 */
	public List<TypeInfo> getValidTypes(Long listId) {
		ComponentListConfig cfg = getComponentListConfig(listId);
		
		List<String> types = cfg.getValidComponentTypes();
		Locale locale = getLocale();
		ArrayList<TypeInfo> result = new ArrayList<TypeInfo>();
		for (String type : types) {
			String description = messageSource.getMessage("component." + type,
					null, FormatUtils.xmlToTitleCase(type), locale);

			result.add(new TypeInfo(type, description));
		}
		return result;
	}

	/**
	 * Creates a new Component and inserts it in the list identified
	 * by the given id.
	 */
	public String insertComponent(Long listId, int position, String type,
			Map<String, String> properties) 
			throws RequestContextExpiredException {

		ComponentList componentList = componentDao.loadComponentList(listId);
		Component component = createComponent(type, properties);
		componentList.insertComponent(component, position);
		componentDao.updateComponentList(componentList);
		return renderComponent(component);
	}

	/**
	 * Creates a new Component of the given type.
	 *
	 * @param type The type of the version to create
	 * @param properties Properties of the version to create
	 * @return The newly created component
	 */
	private Component createComponent(String type, Map<String, String> properties) {
		Component component = repository.createComponent(type, properties);
		componentDao.saveContent(component);
		return component;
	}
	
	public String setType(Long componentId, String type) 
			throws RequestContextExpiredException {
		
		Component component = componentDao.loadComponent(componentId);
		component.setType(type);
		ComponentRenderer renderer = repository.getRenderer(type);
		component.wrapValues(renderer.getDefaults());
		return renderComponent(component);
	}
	
	public String renderComponent(Long componentId) 
			throws RequestContextExpiredException {
		
		Component component = componentDao.loadComponent(componentId);
		return renderComponent(component);
	}

	private String renderComponent(Component component)
			throws RequestContextExpiredException {

		try {
			ComponentRenderer renderer = repository.getRenderer(component);
			ComponentList list = component.getList();
			
			StringWriter sw = new StringWriter();
			HttpServletRequest request = getWrappedRequest(list.getId());
			HttpServletResponse response = getCapturingResponse(sw);
			
			editModeRenderer.renderComponent(renderer, component,
					list.indexOf(component), list.getSize(), request, response);
			
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

	public void moveComponent(Long componentId, Long nextComponentId) {
		Component component = componentDao.loadComponent(componentId);
		ComponentList componentList = component.getList();
		List<Component> components = componentList.getComponents();
		components.remove(component);
		if (nextComponentId != null) {
			for (int i = 0; i < components.size(); i++) {
				if (components.get(i).getId().equals(nextComponentId)) {
					components.add(i, component);
					break;
				}
			}
		}
		else {
			components.add(component);
		}
		componentList.getContainer().setDirty(true);
		componentDao.updateComponentList(componentList);
	}

	public void deleteComponent(Long componentId) {
		Component component = componentDao.loadComponent(componentId);
		ComponentList componentList = component.getList();
		List<Component> components = componentList.getComponents();
		components.remove(component);
		componentList.getContainer().setDirty(true);
		componentDao.updateComponentList(componentList);
	}

	public void publish(Long[] listIds, Long[] containerIds) {
		HashSet<ContentContainer> containers = new HashSet<ContentContainer>();
		if (containerIds != null) {
			for (Long id : containerIds) {
				if (id != null) {
					containers.add(componentDao.loadContentContainer(id));
				}
			}
		}
		if (listIds != null) {
			for (Long id : listIds) {
				if (id != null) {
					ComponentList list = componentDao.loadComponentList(id);
					containers.add(list.getContainer());
				}
			}
		}
		for (ContentContainer container : containers) {
			componentDao.publishContainer(container);
		}
	}

	public void discard(Long[] listIds, Long[] containerIds) {
		HashSet<ContentContainer> containers = new HashSet<ContentContainer>();
		if (containerIds != null) {
			for (Long id : containerIds) {
				if (id != null) {
					containers.add(componentDao.loadContentContainer(id));
				}
			}
		}
		if (listIds != null) {
			for (Long id : listIds) {
				if (id != null) {
					ComponentList list = componentDao.loadComponentList(id);
					containers.add(list.getContainer());
				}
			}
		}
		for (ContentContainer container : containers) {
			componentDao.discardContainer(container);
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
		ComponentRequestUtils.touchContext(request, path);
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

	private HttpServletRequest getWrappedRequest(Long listId)
			throws RequestContextExpiredException {

		WebContext ctx = WebContextFactory.get();
		HttpServletRequest request = ctx.getHttpServletRequest();
		String path = ServletUtils.getPath(ctx.getCurrentPage());
		return ComponentRequestUtils.wrapRequest(request, path, listId);
	}
	
	private ComponentListConfig getComponentListConfig(Long listId) {
		WebContext ctx = WebContextFactory.get();
		HttpServletRequest request = ctx.getHttpServletRequest();
		String path = ServletUtils.getPath(ctx.getCurrentPage());
		return ComponentRequestUtils.getContext(request, path, listId).getComponentListConfig();
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

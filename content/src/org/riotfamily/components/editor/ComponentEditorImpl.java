/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.components.editor;

import java.io.StringWriter;
import java.util.Arrays;
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
import org.riotfamily.common.util.Generics;
import org.riotfamily.common.web.support.CapturingResponseWrapper;
import org.riotfamily.components.config.ContentFormRepository;
import org.riotfamily.components.meta.ComponentMetaData;
import org.riotfamily.components.meta.ComponentMetaDataProvider;
import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.ComponentList;
import org.riotfamily.components.model.Content;
import org.riotfamily.components.model.ContentContainer;
import org.riotfamily.components.model.ContentFragment;
import org.riotfamily.components.model.ContentMap;
import org.riotfamily.components.render.component.ComponentRenderer;
import org.riotfamily.components.render.component.EditModeComponentRenderer;
import org.riotfamily.components.support.OverrideMethodRequestWrapper;
import org.riotfamily.core.security.AccessController;
import org.riotfamily.core.security.auth.RiotUser;
import org.riotfamily.core.security.session.LoginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private Logger log = LoggerFactory.getLogger(ComponentEditorImpl.class);

	private ComponentRenderer renderer;

	private ComponentMetaDataProvider metaDataProvider;
	
	private Map<String, Map<String, Object>> tinyMCEProfiles;

	private MessageSource messageSource;
	
	
	public ComponentEditorImpl(ComponentRenderer renderer, 
			ComponentMetaDataProvider metaDataProvider,
			ContentFormRepository formRepository) {
		
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
		ContentMap content = Content.loadFragment(contentId);
		Object value = content.get(property);
		return value != null ? value.toString() : null;
	}

	/**
	 * Sets the given property to a new value.
	 */
	@RemoteMethod
	public void updateText(String contentId, String property, String text) {
		ContentMap content = Content.loadFragment(contentId);
		assertIsEditGranted(content);
		content.put(property, text);
		nofifyUsers();
	}

	/**
	 *
	 */
	@RemoteMethod
	public String[] updateTextChunks(String componentId, String property,
			String[] chunks) {

		String[] html = new String[chunks.length];
		Component component = Component.load(componentId);
		assertIsEditGranted(component);
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
		nofifyUsers();
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
		assertIsEditGranted(componentList);
		Component component = createComponent(componentList, type);
		componentList.add(position, component);
		nofifyUsers();
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
		assertIsEditGranted(component);
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
			HttpServletRequest request = new OverrideMethodRequestWrapper(
					WebContextFactory.get().getHttpServletRequest())
					.setMethod("GET");
			
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
	public void moveComponent(String componentId, String prevComponentId) {
		Component component = Component.load(componentId);
		assertIsEditGranted(component);
		component.move(prevComponentId);
		nofifyUsers();
	}

	@RemoteMethod
	public void deleteComponent(String componentId) {
		Component component = Component.load(componentId);
		assertIsEditGranted(component);
		component.delete();
		nofifyUsers();
	}
	
	@RemoteMethod
	public ToolbarState getState(Long[] containerIds) {
		initScriptSession();
		HttpServletRequest request = WebContextFactory.get().getHttpServletRequest();
		ToolbarState state = new ToolbarState();
		if (containerIds != null) {
			for (Long id : containerIds) {
				if (id != null) {
					ContentContainer container = ContentContainer.load(id);
					if (AccessController.isGranted("edit", container.getOwner(), request)) {
						state.setEdit(true);
					}
					if (AccessController.isGranted("publish", container.getOwner(), request)) {
						state.getContainerIds().add(container.getId());
						if (container.isDirty()) {
							state.setDirty(true);
						}
					}
				}
			}
		}
		return state;
	}

	@RemoteMethod
	public void publish(Long[] containerIds) {
		if (containerIds != null) {
			for (Long id : Generics.newHashSet(Arrays.asList(containerIds))) {
				if (id != null) {
					ContentContainer container = ContentContainer.load(id);
					assertIsPublishGranted(container);
					container.publish();
				}
			}
		}
		nofifyUsers();
	}

	@RemoteMethod
	public void discard(Long[] containerIds) {
		if (containerIds != null) {
			for (Long id : Generics.newHashSet(Arrays.asList(containerIds))) {
				if (id != null) {
					ContentContainer container = ContentContainer.load(id);
					assertIsEditGranted(container);
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
	
	private void assertIsEditGranted(ContentFragment fragment) {
		assertIsEditGranted(fragment.getContent().getContainer());
	}
	
	private void assertIsEditGranted(ContentContainer container) {
		AccessController.assertIsGranted("edit", container.getOwner(), 
				WebContextFactory.get().getHttpServletRequest());
	}
	
	private void assertIsPublishGranted(ContentContainer container) {
		AccessController.assertIsGranted("publish", container.getOwner(), 
				WebContextFactory.get().getHttpServletRequest());
	}
	
	private HttpServletResponse getCapturingResponse(StringWriter sw) {
		WebContext ctx = WebContextFactory.get();
		return new CapturingResponseWrapper(ctx.getHttpServletResponse(), sw);
	}
	
	private void initScriptSession() {
		WebContext webContext = WebContextFactory.get();
		HttpServletRequest request = webContext.getHttpServletRequest();

		ScriptSession currentSession = webContext.getScriptSession();
		String host = request.getServerName();
		RiotUser user = AccessController.getCurrentUser();

		currentSession.setAttribute("host", host);
		currentSession.setAttribute("userId", user.getUserId());
	}

	private void nofifyUsers() {
		WebContext webContext = WebContextFactory.get();
		HttpServletRequest request = webContext.getHttpServletRequest();

		final ScriptSession currentSession = webContext.getScriptSession();
		final RiotUser user = AccessController.getCurrentUser();
		final String host = request.getServerName();
		String userName = "";
		if (user.getName() != null) {
			userName = " (" + user.getName() + ")";
		}
		
		Locale locale = RequestContextUtils.getLocale(request);
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
					return !user.getUserId().equals(session.getAttribute("userId")) 
							&& currentSession.getPage().equals(session.getPage())
							&& host.equals(session.getAttribute("host"));
				}
			}, 
			new Runnable() {
				public void run() {
					ScriptSessions.addFunctionCall("riot.showNotification", message);
				}
			});
	}
	
}

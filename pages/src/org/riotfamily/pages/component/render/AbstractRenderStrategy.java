package org.riotfamily.pages.component.render;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.pages.component.Component;
import org.riotfamily.pages.component.ComponentList;
import org.riotfamily.pages.component.ComponentListConfiguration;
import org.riotfamily.pages.component.ComponentVersion;
import org.riotfamily.pages.component.VersionContainer;
import org.riotfamily.pages.component.impl.MissingComponent;

public class AbstractRenderStrategy implements RenderStrategy {
	
	protected Log log = LogFactory.getLog(getClass());
	
	protected ComponentListConfiguration config;
	
	protected HttpServletRequest request;
	
	protected HttpServletResponse response;
	
	protected PrintWriter out;
	
	
	public AbstractRenderStrategy(ComponentListConfiguration config, 
			HttpServletRequest request, HttpServletResponse response) 
			throws IOException {
		
		this.config = config;
		this.request = request;
		this.response = response;
		out = response.getWriter();
	}
	
	public void render(String path, String key) throws IOException {
		ComponentList list = getComponentList(path, key);
		if (list != null) {
			renderComponentList(list);
		}
		else {
			onListNotFound(path, key);
		}
	}
	
	public void render(ComponentList list) throws IOException {
		renderComponentList(list);
	}

	protected void onListNotFound(String path, String key) throws IOException {
		log.debug("No ComponentList found with path " 
					+ path + " and key " + key);
	}
	
	/**
	 * Returns the ComponentList to be rendered. The default implementation
	 * uses the controller's ComponentDao to look up a list for the current 
	 * path/key-combination.
	 */
	protected ComponentList getComponentList(String path, String key) {
		log.debug("Looking up ComponentList " + path + '#' + key);
		return config.getComponentDao().findComponentList(path, key);
	}	
	
	/**
	 * Renders the given list. The default implementation calls 
	 * {@link #getComponentsToRender(ComponentList)} and passes the result
	 * to {@link #renderComponents(List)}.
	 */
	protected void renderComponentList(ComponentList list) throws IOException {
		List components = getComponentsToRender(list);
		renderComponents(components);
	}
	
	/**
	 * Renders the given list. The default implementation iterates over the 
	 * given list and calls {@link #renderContainer(VersionContainer)} for each
	 * item. If the list is empty or null, {@link #onEmptyComponentList()} is
	 * invoked.
	 */
	protected final void renderComponents(List components) throws IOException {
		if (components == null || components.isEmpty()) {
			onEmptyComponentList();
			return;
		}
		
		int i = 0;
		Iterator it = components.iterator();
		while (it.hasNext()) {
			VersionContainer container = (VersionContainer) it.next();
			renderContainer(container, getPositionalClassName(i++, !it.hasNext()));
		}
	}
	
	protected void onEmptyComponentList() throws IOException {
	}
	
	/**
	 * Returns a list of VersionContainers. The default implementation
	 * simply returns the list's live components.
	 */
	protected List getComponentsToRender(ComponentList list) {
		return list.getLiveList();
	}
	
	/**
	 * Renders the given VersionContainer. The default implementation calls 
	 * {@link #getVersionToRender(VersionContainer) getVersionToRender()} and 
	 * passes the result to {@link #renderComponentVersion(ComponentVersion)
	 * renderComponentVersion()} (if not null). 
	 */
	
	protected void renderContainer(VersionContainer container, 
			String positionClassName) throws IOException {

		ComponentVersion version = getVersionToRender(container);
		if (version != null) {
			renderComponentVersion(version, positionClassName);
		}
	}
		
	/**
	 * Returns the ComponentVersion to render. The default implementation 
	 * simply returns the component's live version.
	 */
	protected ComponentVersion getVersionToRender(VersionContainer container) {
		return container.getLiveVersion();
	}
	
	/**
	 * Renders the given ComponentVersion. 
	 * @throws IOException 
	 */
	protected final void renderComponentVersion(ComponentVersion version, 
			String positionClassName) throws IOException {
		
		String type = version.getType(); 
		Component component = config.getRepository().getComponent(type);
		if (component == null) {
			log.error("No such Component: " + type);
			component = new MissingComponent(type);
		}
		renderComponent(component, version, positionClassName);
	}
	
	protected void renderComponent(Component component, 
			ComponentVersion version, String positionClassName) 
			throws IOException {
		
		component.render(version, positionClassName, config, request, response);
	}
	
	protected String getPositionalClassName(int position, boolean last) {
		StringBuffer sb = new StringBuffer("component-").append(position + 1);
		if (last) {
			sb.append(" last-component");
		}
		return sb.toString();
	}

}

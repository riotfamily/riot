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
package org.riotfamily.components.render;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.components.ComponentList;
import org.riotfamily.components.ComponentRepository;
import org.riotfamily.components.ComponentVersion;
import org.riotfamily.components.Location;
import org.riotfamily.components.VersionContainer;
import org.riotfamily.components.config.ComponentListConfiguration;
import org.riotfamily.components.context.PageRequestUtils;
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.components.editor.ComponentFormRegistry;

public class EditModeRenderStrategy extends PreviewModeRenderStrategy {
	
	private static final Log log = LogFactory.getLog(EditModeRenderStrategy.class);
	
	private ComponentFormRegistry formRegistry;
	
	public EditModeRenderStrategy(ComponentDao dao, 
			ComponentRepository repository, ComponentFormRegistry formRegistry,
			ComponentListConfiguration config,
			HttpServletRequest request, HttpServletResponse response) 
			throws IOException {
		
		super(dao, repository, config, request, response);
		this.formRegistry = formRegistry;
	}

	public void renderComponentVersion(ComponentVersion version) 
			throws IOException {
		
		request.setAttribute(EDIT_MODE_ATTRIBUTE, Boolean.TRUE);
		VersionContainer c = version.getContainer();
		List components = getComponentsToRender(c.getList());
		int position = components.indexOf(c);
		boolean last = position == components.size() - 1;
		renderComponentVersion(version, getPositionalClassName(position, last));
	}
	
	/**
	 * Overrides the default implementation to render a DIV tag around the 
	 * actual list. The DIV has attributes that are required for the 
	 * Riot-Toolbar JavaScript.
	 */
	protected void renderComponentList(ComponentList list) throws IOException {
		boolean renderOuterDiv = PageRequestUtils.storeContext(
				request, list.getId(), 120000);
		
		request.setAttribute(EDIT_MODE_ATTRIBUTE, Boolean.TRUE);
		if (renderOuterDiv) {
			out.print("<div riot:listId=\"");
			out.print(list.getId());
			out.print("\" riot:controllerId=\"");
			String uri = ServletUtils.getIncludeUri(request);
			uri = uri.substring(request.getContextPath().length());
			out.print(uri);
			out.print('"');
			if (config.getMinComponents() != null) {
				out.print(" riot:minComponents=\"");
				out.print(config.getMinComponents());
				out.print('"');
			}
			if (config.getMaxComponents() != null) {
				out.print(" riot:maxComponents=\"");
				out.print(config.getMaxComponents());
				out.print('"');
			}
			if (list.isDirty()) {
				out.print(" riot:dirty=\"true\"");
			}
			out.print(" class=\"riot-components riot-component-list\">");
			super.renderComponentList(list);
			out.print("</div>");
		}
		else {
			super.renderComponentList(list);
		}
	}
	
	/**
	 * Overrides the default implementation to create a new list if no existing
	 * list is found.
	 * 
	 * @see #createNewList(Location) 
	 */
	protected ComponentList getComponentList(Location location) {
		ComponentList list = super.getComponentList(location);
		if (list == null) {
			list = createNewList(location);
		}
		return list;
	}
	
	/**
	 * Creates a new ComponentList with an initial component set as defined by
	 * the controller.
	 */
	protected ComponentList createNewList(Location location) {
		ComponentList list = new ComponentList();
		list.setLocation(new Location(location));
		list.setParent(parent);
		dao.saveComponentList(list);
		log.debug("New ComponentList created: " + list);
		String[] initialTypes = config.getInitialComponentTypes();
		if (initialTypes != null) {
			List containers = new ArrayList();
			for (int i = 0; i < initialTypes.length; i++) {
				VersionContainer container = new VersionContainer();
				ComponentVersion live = new ComponentVersion(initialTypes[i]);
				live.setContainer(container);
				container.setList(list);
				container.setLiveVersion(live);
				containers.add(container);
			}
			list.setLiveContainers(containers);
			dao.updateComponentList(list);
		}
		return list;
	}
	
	/**
	 * Overrides the default implementation to render a DIV tag around the 
	 * actual component. The DIV has attributes that are required for the 
	 * Riot-Toolbar JavaScript.
	 * @throws IOException 
	 */
	protected void renderContainer(VersionContainer container, 
			String positionClassName) throws IOException {
		
		ComponentVersion version = getVersionToRender(container);
		out.print("<div riot:containerId=\"");
		out.print(container.getId());
		out.print("\" riot:componentType=\"");
		out.print(version.getType());
		out.print('"');
		
		String type = version.getType(); 
		String formUrl = formRegistry.getFormUrl(type, container.getId());
		if (formUrl != null) {
			out.print(" riot:form=\"");
			out.print(formUrl);
			out.print('"');
		}
		
		out.print(" class=\"riot-component riot-component-");
		out.print(version.getType());
		out.print("\">");
		renderComponentVersion(version, positionClassName);
		out.print("</div>");
	}
	
	protected RenderStrategy getStrategyForParentList() throws IOException {
		return new InheritingRenderStrategy(dao, repository, config, 
				request, response);
	}

}

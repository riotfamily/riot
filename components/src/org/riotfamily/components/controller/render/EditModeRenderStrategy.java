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
package org.riotfamily.components.controller.render;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.markup.DocumentWriter;
import org.riotfamily.common.markup.Html;
import org.riotfamily.common.markup.TagWriter;
import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.components.config.ComponentListConfiguration;
import org.riotfamily.components.config.ComponentRepository;
import org.riotfamily.components.context.PageRequestUtils;
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.components.model.ComponentList;
import org.riotfamily.components.model.ComponentVersion;
import org.riotfamily.components.model.Location;
import org.riotfamily.components.model.VersionContainer;

public class EditModeRenderStrategy extends PreviewModeRenderStrategy {

	private static final Log log = LogFactory.getLog(EditModeRenderStrategy.class);

	public EditModeRenderStrategy(ComponentDao dao,
			ComponentRepository repository, 
			ComponentListConfiguration config,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {

		super(dao, repository, config, request, response);
	}

	public void renderComponentVersion(ComponentVersion version)
			throws IOException {

		VersionContainer c = version.getContainer();
		List components = getComponentsToRender(c.getList());
		int position = components.indexOf(c);
		boolean last = position == components.size() - 1;
		renderComponentVersion(version, getPositionalClassName(position, last));
	}

	/**
	 * Overrides the default implementation to render a DIV tag around the
	 * actual list. The DIV has attributes that are required for the
	 * Riot toolbar JavaScript.
	 */
	protected void renderComponentList(ComponentList list) throws IOException {
		DocumentWriter wrapper = new DocumentWriter(out);
		
		boolean renderOuterDiv = PageRequestUtils.createAndStoreContext(
				request, list.getId().toString(), 120000);

		if (renderOuterDiv) {
			wrapper.start(Html.DIV)
				.attribute(Html.COMMON_CLASS, "riot-controller")
				.attribute("riot:contextKey", list.getId().toString())
				.attribute("riot:controllerId", 
						ServletUtils.getPathWithinApplication(request));
		}
		
		String className = "riot-list riot-component-list";
		if (parent == null) {
			className += " riot-toplevel-list";
		}
		if (list.isDirty()) {
			className += " riot-dirty";
		}
		
		wrapper.start(Html.DIV)
			.attribute(Html.COMMON_CLASS, className)
			.attribute("riot:listId", list.getId().toString());
		
		if (config.getMinComponents() != null) {
			wrapper.attribute("riot:minComponents", 
					config.getMinComponents().intValue());
		}
		if (config.getMaxComponents() != null) {
			wrapper.attribute("riot:maxComponents", 
					config.getMaxComponents().intValue());
		}
		
		wrapper.body();
		super.renderComponentList(list);
		wrapper.closeAll();
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
		}
		dao.saveComponentList(list);
		log.debug("New ComponentList created: " + list);
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
		String type = version.getType();
		String formUrl = repository.getFormUrl(type, container.getId());
		
		String className = "riot-list-component riot-component " +
				"riot-component-" + type;
		
		if (formUrl != null) {
			className += " riot-form";
		}
		
		TagWriter wrapper = new TagWriter(out);
		wrapper.start(Html.DIV)
				.attribute(Html.COMMON_CLASS, className)
				.attribute("riot:containerId", container.getId().toString())
				.attribute("riot:componentType", type)
				.attribute("riot:form", formUrl)
				.body();

		renderComponentVersion(version, positionClassName);
		wrapper.end();
	}

	protected RenderStrategy getStrategyForParentList() throws IOException {
		return new InheritingRenderStrategy(dao, repository, config,
				request, response);
	}

}

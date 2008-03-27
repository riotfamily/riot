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
import org.riotfamily.cachius.CacheService;
import org.riotfamily.common.markup.DocumentWriter;
import org.riotfamily.common.markup.Html;
import org.riotfamily.common.markup.TagWriter;
import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.components.cache.ComponentCacheUtils;
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

	private RenderStrategy parentStrategy;
	
	private CacheService cacheService;
	
	public EditModeRenderStrategy(ComponentDao dao,
			ComponentRepository repository, 
			ComponentListConfiguration config,
			CacheService cacheService) {

		super(dao, repository, config);
		parentStrategy = new InheritingRenderStrategy(dao, repository, config);
		this.cacheService = cacheService;
	}

	/**
	 * Overrides the default implementation to render a DIV tag around the
	 * actual list. The DIV has attributes that are required for the
	 * Riot toolbar JavaScript.
	 */
	protected void renderComponentList(ComponentList list, 
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {
		
		DocumentWriter wrapper = new DocumentWriter(response.getWriter());
		
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
		if (getParentContainer(request) == null) {
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
		super.renderComponentList(list, request, response);
		wrapper.closeAll();
	}

	/**
	 * Overrides the default implementation to create a new list if no existing
	 * list is found.
	 *
	 * @see #createNewList(Location)
	 */
	protected ComponentList getComponentList(Location location, 
			HttpServletRequest request) {
		
		ComponentList list = super.getComponentList(location, request);
		if (list == null) {
			list = createNewList(location, request);
		}
		return list;
	}

	/**
	 * Creates a new ComponentList with an initial component set as defined by
	 * the controller.
	 * @param request 
	 */
	protected ComponentList createNewList(Location location,
			HttpServletRequest request) {
		
		ComponentList list = new ComponentList();
		list.setLocation(new Location(location));
		list.setParent(getParentContainer(request));
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
		
		ComponentCacheUtils.invalidateList(cacheService, list);
		return list;
	}

	/**
	 * Overrides the default implementation to render a DIV tag around the
	 * actual component. The DIV has attributes that are required for the
	 * Riot toolbar JavaScript.
	 * @throws IOException
	 */
	protected void renderContainer(VersionContainer container,
			String positionClassName, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		ComponentVersion version = getVersionToRender(container);
		String type = version.getType();
		String formUrl = repository.getFormUrl(type, container.getId());
		
		String className = "riot-list-component riot-component " +
				"riot-component-" + type;
		
		if (formUrl != null) {
			className += " riot-form";
		}
		
		TagWriter wrapper = new TagWriter(response.getWriter());
		wrapper.start(Html.DIV)
				.attribute(Html.COMMON_CLASS, className)
				.attribute("riot:containerId", container.getId().toString())
				.attribute("riot:componentType", type)
				.attribute("riot:form", formUrl)
				.body();

		renderComponentVersion(version, positionClassName, request, response);
		wrapper.end();
	}

	protected RenderStrategy getStrategyForParentList() {
		return parentStrategy;
	}

}

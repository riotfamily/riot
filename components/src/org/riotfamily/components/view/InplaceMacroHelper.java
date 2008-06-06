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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.components.view;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.components.EditModeUtils;
import org.riotfamily.components.config.ComponentListConfig;
import org.riotfamily.components.config.ComponentRepository;
import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.ContentContainer;
import org.riotfamily.components.render.list.ComponentListRenderer;
import org.riotfamily.riot.dao.RiotDao;
import org.riotfamily.riot.list.RiotDaoService;
import org.springframework.util.ClassUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class InplaceMacroHelper {

	private HttpServletRequest request;
	
	private HttpServletResponse response;

	private List<String> toolbarScripts;

	private List<DynamicToolbarScript> dynamicToolbarScripts;
	
	private ComponentRepository componentRepository;
	
	private ComponentListRenderer componentListRenderer;
	
	private RiotDaoService riotDaoService;

	public InplaceMacroHelper(HttpServletRequest request,
			HttpServletResponse response, 
			List<String> toolbarScripts,
			List<DynamicToolbarScript> dynamicToolbarScripts, 
			ComponentRepository repository,
			ComponentListRenderer componentListRenderer,
			RiotDaoService riotDaoService) {

		this.request = request;
		this.response = response;
		this.toolbarScripts = toolbarScripts;
		this.dynamicToolbarScripts = dynamicToolbarScripts;
		this.componentRepository = repository;
		this.componentListRenderer = componentListRenderer;
		this.riotDaoService = riotDaoService;
	}

	public boolean isEditMode() {
		return EditModeUtils.isEditMode(request);
	}
	
	public boolean isLiveModePreview() {
		return EditModeUtils.isLiveModePreview(request);
	}

	public List<String> getToolbarScripts() {
		return this.toolbarScripts;
	}

	public String getInitScript() {
		StringBuffer sb = new StringBuffer();
		for (DynamicToolbarScript script : dynamicToolbarScripts) {
			String js = script.generateJavaScript(request);
			if (js != null) {
				sb.append(js).append('\n');
			}
		}
		return sb.toString();
	}
	
	public String getFormUrl(String formId, ContentContainer container) {
		return componentRepository.getFormUrl(formId, container.getId(), 
				container.getPreviewVersion().getId());
	}
	
	public String enableOutputWrapping() {
		request.setAttribute(ComponentFreeMarkerView.WRAP_OUTPUT_ATTRIBUTE, Boolean.TRUE);
		return "";
	}
	
	public String getObjectId(String listId, Object object) {
		RiotDao dao = riotDaoService.getDao(listId);
		return dao.getObjectId(object);
	}
	
	public String getDefaultListId(Object object) {
		return riotDaoService.getDefaultListId(
				ClassUtils.getUserClass(object));
	}
	
	public String renderComponentList(ContentContainer container, 
			String key, Integer minComponents, Integer maxComponents,
			List<String> initalComponentTypes, 
			List<String> validComponentTypes)
			throws Exception {
		
		ComponentListConfig config = new ComponentListConfig(minComponents,
				maxComponents, initalComponentTypes, validComponentTypes);
		
		componentListRenderer.renderComponentList(container, key, config, 
				request, response);
		
		return "";
	}
	
	public String renderNestedComponentList(Component parent, 
			String key, Integer minComponents, Integer maxComponents,
			List<String> initalComponentTypes, 
			List<String> validComponentTypes)
			throws Exception {
		
		ComponentListConfig config = new ComponentListConfig(minComponents,
				maxComponents, initalComponentTypes, validComponentTypes);
		
		componentListRenderer.renderNestedComponentList(parent, key, config, 
				request, response);
		
		return "";
	}
}

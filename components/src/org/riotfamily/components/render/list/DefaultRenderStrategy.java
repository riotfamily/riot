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
package org.riotfamily.components.render.list;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.log.RiotLog;
import org.riotfamily.common.log.RiotLog;
import org.riotfamily.components.config.ComponentListConfig;
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.ComponentList;
import org.riotfamily.components.render.component.ComponentRenderer;

public class DefaultRenderStrategy implements RenderStrategy {
	
	public static final String INHERTING_COMPONENT = "inherit";
	
	protected RiotLog log = RiotLog.get(getClass());
	
	protected ComponentDao dao; 
	
	private ComponentRenderer renderer;
	
	public DefaultRenderStrategy(ComponentDao dao, ComponentRenderer renderer) {
		this.dao = dao;
		this.renderer = renderer;
	}
		
	public void render(ComponentList list,
			ComponentListConfig config,
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {
		
		List<Component> components = list.getComponents();
		if (components == null || components.isEmpty()) {
			onEmptyComponentList(config, request, response);
			return;
		}
		
		int i = 0;
		for (Component component : components) {
			renderComponent(component, i++, components.size(), config, 
					request, response);
		}
	}
	
	protected void onEmptyComponentList(ComponentListConfig config, 
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {

	}
	
	protected void renderComponent(Component component, 
			int position, int listSize, ComponentListConfig config, 
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {
		
		renderer.render(component, position, listSize, request, response);
	}

}

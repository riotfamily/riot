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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.components.config.ComponentListConfiguration;
import org.riotfamily.components.config.ComponentRepository;
import org.riotfamily.components.config.component.ComponentRenderer;
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.ComponentList;

public class PreviewModeRenderStrategy extends AbstractRenderStrategy {

	public PreviewModeRenderStrategy(ComponentDao dao, 
			ComponentRepository repository, ComponentListConfiguration config) {
		
		super(dao, repository, config);
	}
	
	/**
	 * Overrides the default implementation to return the preview components 
	 * in case the list is marked as dirty.
	 */
	protected List getComponentsToRender(ComponentList list) {
		if (list.isDirty()) {
			log.debug("List is dirty - rendering preview containers");
			return list.getPreviewComponents();
		}
		else {
			log.debug("List is *NOT* dirty - rendering live containers");
			return list.getLiveComponents();
		}
	}
	
	protected void renderComponent(ComponentRenderer renderer, 
			Component component, int position, int listSize, 
			HttpServletRequest request, HttpServletResponse response) 
			throws Exception {
		
		renderer.render(component, true, position, listSize, request, response);
	}	
}

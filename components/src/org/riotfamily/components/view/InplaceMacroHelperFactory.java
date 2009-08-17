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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.view.MacroHelperFactory;
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.components.render.list.ComponentListRenderer;
import org.riotfamily.riot.list.RiotDaoService;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class InplaceMacroHelperFactory implements MacroHelperFactory {

	private List<String> toolbarScripts = Collections.emptyList();
	
	private List<DynamicToolbarScript> dynamicToolbarScripts = Collections.emptyList();

	private ComponentListRenderer componentListRenderer;

	private RiotDaoService riotDaoService;
	
	private ComponentDao componentDao;
	
	public InplaceMacroHelperFactory(
			ComponentListRenderer componentListRenderer,
			RiotDaoService riotDaoService,
			ComponentDao componentDao) {
		
		this.componentListRenderer = componentListRenderer;
		this.riotDaoService = riotDaoService;
		this.componentDao = componentDao;
	}
	
	public void setToolbarScripts(List<String> toolbarScripts) {
		this.toolbarScripts = toolbarScripts;
	}
	
	public void setDynamicToolbarScripts(List<DynamicToolbarScript> dynamicToolbarScripts) {
		this.dynamicToolbarScripts = dynamicToolbarScripts;
	}

	public Object createMacroHelper(HttpServletRequest request,
			HttpServletResponse response, Map<String, ?> model) {

		
		return new InplaceMacroHelper(request, response, toolbarScripts, 
				dynamicToolbarScripts, componentListRenderer, riotDaoService,
				componentDao);
	}
}

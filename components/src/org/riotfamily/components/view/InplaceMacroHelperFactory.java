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

import java.util.Collection;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.view.MacroHelperFactory;
import org.riotfamily.components.config.ComponentRepository;
import org.riotfamily.riot.list.RiotDaoService;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class InplaceMacroHelperFactory implements MacroHelperFactory {

	private Collection toolbarScripts = Collections.EMPTY_LIST;

	private ComponentRepository componentRepository;

	private RiotDaoService riotDaoService;
	
	public InplaceMacroHelperFactory(ComponentRepository componentRepository,
			RiotDaoService riotDaoService) {
		
		this.componentRepository = componentRepository;
		this.riotDaoService = riotDaoService;
	}

	public void setToolbarScripts(Collection toolbarScripts) {
		this.toolbarScripts = toolbarScripts;
	}

	public Object createMacroHelper(HttpServletRequest request,
			HttpServletResponse response) {

		return new InplaceMacroHelper(request, toolbarScripts, 
				componentRepository, riotDaoService);
	}
}

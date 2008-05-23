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

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.components.EditModeUtils;
import org.riotfamily.components.config.ComponentRepository;
import org.riotfamily.riot.dao.RiotDao;
import org.riotfamily.riot.list.RiotDaoService;
import org.riotfamily.riot.security.AccessController;
import org.springframework.util.ClassUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class InplaceMacroHelper {

	private HttpServletRequest request;

	private List toolbarScripts;

	private List dynamicToolbarScripts;
	
	private ComponentRepository componentRepository;
	
	private RiotDaoService riotDaoService;

	public InplaceMacroHelper(HttpServletRequest request,
			List toolbarScripts, List dynamicToolbarScripts, 
			ComponentRepository repository,
			RiotDaoService riotDaoService) {

		this.request = request;
		this.toolbarScripts = toolbarScripts;
		this.dynamicToolbarScripts = dynamicToolbarScripts;
		this.componentRepository = repository;
		this.riotDaoService = riotDaoService;
	}

	public boolean isEditMode() {
		return EditModeUtils.isEditMode(request);
	}

	public List getToolbarScripts() {
		return this.toolbarScripts;
	}

	public String getInitScript() {
		StringBuffer sb = new StringBuffer();
		Iterator it = dynamicToolbarScripts.iterator();
		while (it.hasNext()) {
			DynamicToolbarScript script = (DynamicToolbarScript) it.next();
			String js = script.generateJavaScript(request);
			if (js != null) {
				sb.append(js).append('\n');
			}
		}
		return sb.toString();
	}
	
	public String getFormUrl(String formId, Long containerId) {
		return componentRepository.getFormUrl(formId, containerId);
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
	
	public boolean isEditGranted() {
		return AccessController.isGranted("toolbarEdit", request);
	}
	
	public boolean isPublishGranted() {
		return AccessController.isGranted("toolbarPublish", request);
	}
}

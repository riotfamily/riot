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
package org.riotfamily.components.editor;

import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.components.config.ComponentRepository;
import org.riotfamily.components.config.component.Component;
import org.riotfamily.components.model.ComponentVersion;
import org.riotfamily.components.service.ComponentService;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.factory.FormRepository;
import org.springframework.web.bind.ServletRequestUtils;

/**
 * Controller that displays a form to edit the properties of a ComponentVersion.
 *
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class ComponentFormController extends AbstractComponentFormController {

	private ComponentService componentService;
	
	private ComponentRepository componentRepository;

	private String containerIdAttribute = "containerId";

	public ComponentFormController(FormRepository formRepository,
			ComponentRepository componentRepository,
			ComponentService componentService) {

		super(formRepository);
		this.componentRepository = componentRepository;
		this.componentService = componentService;
	}
	
	protected void initForm(Form form, HttpServletRequest request) {
		super.initForm(form, request);
		Enumeration names = request.getParameterNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			form.setAttribute(name, request.getParameter(name));
		}
	}

	protected ComponentVersion getVersion(HttpServletRequest request) {
		Long id = new Long((String) request.getAttribute(containerIdAttribute));
		boolean live = ServletRequestUtils.getBooleanParameter(request, "live", false);
		return componentService.getOrCreateVersion(id, live);
	}

	protected Object getFormBackingObject(HttpServletRequest request) {
		ComponentVersion version = getVersion(request);
		Component component = componentRepository.getComponent(version);
		return component.buildModel(version);
	}

	protected void onSave(Object object, HttpServletRequest request) {
		ComponentVersion version = getVersion(request);
		Map properties = (Map) object;
		componentService.updateComponentProperties(version, properties);
	}

}
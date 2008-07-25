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
package org.riotfamily.components.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.ComponentList;
import org.riotfamily.website.form.SimpleMailFormController;

public class ComponentMailFormController extends SimpleMailFormController {

	private ComponentDao dao;
	
	@Override
	protected String getMailText(HttpServletRequest request,
			Map<String, String> data) {
		
		/*
		Long id = null;
		ComponentList list = dao.loadComponentList(id);
		for (Component component : list.getComponents()) {
			Map<String, Object> props = component.unwrap();
			String label = (String) props.get("label");
			if (label != null) {
				label = FormatUtils.stripTagsAndSpaces(label);
				String value = request.getParameter("f" + component.getId());
				if (value.startsWith("opt-")) {
					props.get("options");
				}
			}
		}
		*/
		return null;
	}
}

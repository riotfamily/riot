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

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.util.Generics;
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.components.model.Component;
import org.riotfamily.components.model.ComponentList;
import org.riotfamily.website.form.AbstractMailFormController;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.ServletRequestUtils;

public class ComponentMailFormController extends AbstractMailFormController {

	private Log log = LogFactory.getLog(ComponentMailFormController.class);
	
	private ComponentDao dao;
	
	public ComponentMailFormController(ComponentDao dao) {
		this.dao = dao;
	}
	
	@Override
	protected void initBinder(HttpServletRequest request,
			ServletRequestDataBinder binder) throws Exception {
		
		Long id = ServletRequestUtils.getLongParameter(request, "components");
		if (id != null) {
			List<String> requiredFields = Generics.newArrayList();
			ComponentList list = dao.loadComponentList(id);
			for (Component component : list.getComponents()) {
				Map<String, Object> props = component.unwrap();
				if (props.get("required") == Boolean.TRUE) {
					requiredFields.add("f" + component.getId());
				}
			}
			String[] fields = new String[requiredFields.size()];
			binder.setRequiredFields(requiredFields.toArray(fields));
		}
	}
	

	@Override
	protected String getMailText(HttpServletRequest request,
			Map<String, String> data) throws ServletRequestBindingException {

		StringBuilder sb = new StringBuilder();
		Long id = ServletRequestUtils.getLongParameter(request, "components");
		ComponentList list = dao.loadComponentList(id);
		for (Component component : list.getComponents()) {
			Map<String, Object> props = component.unwrap();
			String label = (String) props.get("label");
			if (label != null) {
				sb.append(FormatUtils.stripTagsAndSpaces(label));
				sb.append(": ");
				String name = "f" + component.getId();
				String value = request.getParameter(name);
				for (int i = 2; value != null; i++) {
					sb.append(value).append(' ');
					value = request.getParameter(name + '-' + i);
				}
				sb.append('\n');
			}
		}
		log.info(sb);
		return sb.toString();
	}
}

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

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.forms.factory.FormRepository;
import org.riotfamily.riot.dao.RiotDao;
import org.riotfamily.riot.list.ListRepository;

/**
 * Controller that displays a form to edit the properties of a ComponentVersion.
 *
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class EntityFormController extends AbstractFrontOfficeFormController {

	private String listIdAttribute = "listId";

	private String objectIdAttribute = "objectId";

	private ListRepository listRepository;
	
	public EntityFormController(FormRepository formRepository,
			ListRepository listRepository) {

		super(formRepository);
		this.listRepository = listRepository;
	}

	protected RiotDao getDao(HttpServletRequest request) {
		String listId = (String) request.getAttribute(listIdAttribute);
		return listRepository.getListConfig(listId).getDao();
	}
	
	protected Object getFormBackingObject(HttpServletRequest request) {
		String objectId = (String) request.getAttribute(objectIdAttribute);
		return getDao(request).load(objectId);
	}

	protected void onSave(Object entity, HttpServletRequest request) {
		getDao(request).update(entity);
	}

}
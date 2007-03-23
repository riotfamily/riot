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
package org.riotfamily.components.editor;

import java.util.HashSet;

import org.springframework.util.Assert;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class ComponentFormRegistry {

	private ComponentFormController formController;
	
	private HashSet formIds = new HashSet();
	
	public void clear() {
		formIds.clear();
	}
	
	public void registerFormId(String formId) {
		formIds.add(formId);
	}
	
	public void setFormController(ComponentFormController formController) {
		this.formController = formController;
	}

	public String getFormUrl(String formId, Long containerId) {
		Assert.notNull(formController, "A FormController must be set.");
		if (formIds.contains(formId)) {
			return formController.getUrl(formId, containerId);
		}
		return null;
	}
}

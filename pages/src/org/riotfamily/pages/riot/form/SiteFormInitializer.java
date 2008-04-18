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
package org.riotfamily.pages.riot.form;

import org.riotfamily.forms.Form;
import org.riotfamily.forms.FormInitializer;
import org.riotfamily.forms.factory.FormRepository;
import org.riotfamily.pages.model.Site;

/**
 * FormInitializer that imports form fields defined in content-forms.xml.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 7.0
 */
public class SiteFormInitializer implements FormInitializer {

	private FormRepository repository;

	public SiteFormInitializer(FormRepository repository) {
		this.repository = repository;
	}

	public void initForm(Form form) {
		Site site = (Site) form.getBackingObject();
		SitePropertiesEditor spe = new SitePropertiesEditor(repository, site.getMasterSite());
		form.addElement(spe, "properties");
	}

}

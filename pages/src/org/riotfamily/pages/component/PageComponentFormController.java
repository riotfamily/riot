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
package org.riotfamily.pages.component;

import java.util.Iterator;

import org.riotfamily.components.ComponentRepository;
import org.riotfamily.components.dao.ComponentDao;
import org.riotfamily.components.editor.ComponentFormController;
import org.riotfamily.components.editor.ComponentFormRegistry;
import org.riotfamily.forms.FormRepository;
import org.riotfamily.forms.factory.FormDefinitionException;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class PageComponentFormController extends ComponentFormController {

	private PageComponent pageComponent;
	
	public PageComponentFormController(FormRepository formRepository, 
			ComponentRepository componentRepository,
			ComponentFormRegistry formRegistry,
			ComponentDao componentDao, PlatformTransactionManager tm) {
		
		super(formRepository, componentRepository, formRegistry, componentDao, tm);
	}

	protected void setupForms() {
		pageComponent = new PageComponent();
		getComponentRepository().addComponent(PageComponent.TYPE, pageComponent);
		Iterator it = getFormRepository().getFormIds().iterator();
		while (it.hasNext()) {
			String id = (String) it.next();
			try {
				setupForm(pageComponent, getFormRepository().createForm(id));
			}
			catch (FormDefinitionException e) {
			}
		}
	}
	
}

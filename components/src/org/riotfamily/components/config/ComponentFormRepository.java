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
package org.riotfamily.components.config;

import org.riotfamily.components.model.Component;
import org.riotfamily.components.riot.form.ContentContainerEditorBinder;
import org.riotfamily.forms.EditorBinder;
import org.riotfamily.forms.FormInitializer;
import org.riotfamily.forms.factory.DefaultFormFactory;
import org.riotfamily.forms.factory.FormFactory;
import org.riotfamily.forms.factory.xml.XmlFormRepository;
import org.springframework.validation.Validator;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class ComponentFormRepository extends XmlFormRepository {

	public ComponentFormRepository(ComponentRepository componentRepository) {
		componentRepository.setFormRepository(this);
	}

	public FormFactory createFormFactory(Class beanClass, 
			FormInitializer initializer, Validator validator) {
		
		EditorBinder binder = new ContentContainerEditorBinder(Component.class);
		return new DefaultFormFactory(binder, initializer, validator);
	}
	
}

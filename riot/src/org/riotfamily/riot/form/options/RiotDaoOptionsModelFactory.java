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
package org.riotfamily.riot.form.options;

import java.util.ArrayList;
import java.util.Collection;

import org.riotfamily.forms.Element;
import org.riotfamily.forms.Form;
import org.riotfamily.forms.OptionsModelFactory;
import org.riotfamily.forms.options.OptionsModel;
import org.riotfamily.forms.options.StaticOptionsModel;
import org.riotfamily.riot.dao.RiotDao;
import org.riotfamily.riot.list.support.ListParamsImpl;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class RiotDaoOptionsModelFactory implements OptionsModelFactory {

	public boolean supports(Object model) {
		return model instanceof RiotDao;
	}
	
	public OptionsModel createOptionsModel(Object model, Element element) {
		RiotDao dao = (RiotDao) model;
		Collection values = dao.list(null, new ListParamsImpl());
		Form form = element.getForm();
		//Remove the object being edited from the collection to prevent circular 
		//references. This is only done when editing an existing object and 
		//classes are assignment compatible.
		if (!form.isNew() && dao.getEntityClass().isAssignableFrom(
				form.getEditorBinder().getBeanClass())) {
			
			ArrayList copy = new ArrayList(values);
			copy.remove(form.getBackingObject());
			return new StaticOptionsModel(copy);
		}
		return new StaticOptionsModel(values);
	}
}

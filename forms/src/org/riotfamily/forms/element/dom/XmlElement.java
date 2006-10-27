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
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.forms.element.dom;

import org.riotfamily.forms.bind.EditorBinder;
import org.riotfamily.forms.bind.XmlElementWrapper;
import org.riotfamily.forms.element.core.NestedForm;

public class XmlElement extends NestedForm {

	private String name;
	
	public void setName(String name) {
		this.name = name;
		setEditorBinder(new EditorBinder(new XmlElementWrapper(name)));
	}
	
	public void setBeanClass(Class beanClass) {
	}

	protected void afterBindingSet() {
		if (name == null) {
			setName(getEditorBinding().getProperty());
		}
		super.afterBindingSet();
	}
	
}

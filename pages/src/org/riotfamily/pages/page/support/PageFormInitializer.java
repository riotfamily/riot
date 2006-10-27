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
package org.riotfamily.pages.page.support;

import org.riotfamily.forms.Form;
import org.riotfamily.forms.FormInitializer;
import org.riotfamily.forms.bind.Editor;
import org.riotfamily.forms.element.ContainerElement;
import org.riotfamily.pages.page.Page;

/**
 * FormInitializer that removes the pathComponent field if the edited page 
 * is flaged as system page.
 */
public class PageFormInitializer implements FormInitializer {

	public void initForm(Form form) {
		Page page = (Page) form.getBackingObject();
		if (page != null && page.isSystemPage()) {
			Editor e = form.getEditor("pathComponent");
			if (e != null) {
				ContainerElement container = (ContainerElement) e.getParent();
				container.removeElement(e);
			}
		}
	}

}

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
package org.riotfamily.pages.page;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.riotfamily.pages.page.support.AbstractPage;

/**
 * Non-persitent page. Transient pages are usually created by a 
 * {@link PageMapPostProcessor} and used to display dynamic content.
 */
public class TransientPage extends AbstractPage {
	
	private List childPages;
	
	public TransientPage() {
	}
	
	public Collection getChildPages() {
		return this.childPages;
	}

	public void setChildPages(List childPages) {
		this.childPages = childPages;
	}

	public void addChildPage(Page child) {
		if (childPages == null) {
			childPages = new ArrayList();
		}
		childPages.add(child);
		child.setParentAndUpdateChildPages(this);
		child.updatePath();
	}
	
	public void removeChildPage(Page child) {
		if (child != null) {
			childPages.remove(child);
		}
	}

}

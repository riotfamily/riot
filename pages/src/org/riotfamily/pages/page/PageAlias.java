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

/**
 * Alias for a page. Aliases are created whenever a page (or one of it's 
 * ancestors) is renamed or moved.
 */
public class PageAlias {

	private String path;
	
	private PersistentPage page;
	
	public PageAlias() {
	}
	
	public PageAlias(String path, PersistentPage page) {
		this.path = path;
		this.page = page;
	}

	public PersistentPage getPage() {
		return this.page;
	}

	public void setPage(PersistentPage page) {
		this.page = page;
	}

	public String getPath() {
		return this.path;
	}

	public void setPath(String path) {
		this.path = path;
	}


}
